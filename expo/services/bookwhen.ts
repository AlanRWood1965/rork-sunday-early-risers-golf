import { GolfEvent } from '@/constants/events';

const BOOKWHEN_API_URL = 'https://api.bookwhen.com/v2/events';
const ACCOUNT_SLUG = 'ser-golf';

const COURSE_IMAGES = [
  'https://images.unsplash.com/photo-1587174486073-ae5e5cff23aa?w=800&q=80',
  'https://images.unsplash.com/photo-1535131749006-b7f58c99034b?w=800&q=80',
  'https://images.unsplash.com/photo-1592919505780-303950717480?w=800&q=80',
  'https://images.unsplash.com/photo-1600005082646-28a8e9e1f498?w=800&q=80',
  'https://images.unsplash.com/photo-1593111774240-d529f12cf4bb?w=800&q=80',
  'https://images.unsplash.com/photo-1622396636133-b43e6f94c42a?w=800&q=80',
  'https://images.unsplash.com/photo-1580126755789-85101eba6306?w=800&q=80',
];

const SPECIAL_IMAGE =
  'https://images.unsplash.com/photo-1611374243147-44a702c2d44c?w=800&q=80';

const MONTHS = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December',
];

interface BookwhenAttributes {
  title?: string | null;
  details?: string | null;
  details_html?: string | null;
  location?: string | null;
  location_text?: string | null;
  start_at?: string | null;
  end_at?: string | null;
  all_day?: boolean | null;
  attachments?: { url?: string }[] | null;
  iframe_src?: string | null;
  tags?: string[] | null;
  cancelled?: boolean | null;
  status?: string | null;
}

interface BookwhenEvent {
  id: string;
  type: string;
  attributes: BookwhenAttributes;
}

interface BookwhenResponse {
  data?: BookwhenEvent[];
}

function pad(n: number): string {
  return n < 10 ? `0${n}` : `${n}`;
}

function getDayOfWeek(date: Date): string {
  const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
  return days[date.getDay()];
}

function formatDateDisplay(date: Date): string {
  return `${date.getDate()} ${MONTHS[date.getMonth()]} ${date.getFullYear()}`;
}

function formatTime(date: Date, allDay: boolean): string {
  if (allDay) return 'Full Day Event';
  const h = date.getHours();
  const m = date.getMinutes();
  const period = h >= 12 ? 'PM' : 'AM';
  const hour12 = h % 12 === 0 ? 12 : h % 12;
  const minStr = m === 0 ? '' : `:${pad(m)}`;
  return `${hour12}${minStr} ${period} Tee-off`;
}

function stripHtml(html: string | null | undefined): string {
  if (!html) return '';
  return html
    .replace(/<[^>]+>/g, ' ')
    .replace(/&nbsp;/g, ' ')
    .replace(/&amp;/g, '&')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/\s+/g, ' ')
    .trim();
}

function classify(title: string): 'weekly' | 'special' {
  const lower = title.toLowerCase();
  if (lower.includes('sunday') && lower.includes('killermont')) return 'weekly';
  return 'special';
}

function pickImage(type: 'weekly' | 'special', index: number, title: string): string {
  if (type === 'special') return SPECIAL_IMAGE;
  const lower = title.toLowerCase();
  if (lower.includes('gailes')) return SPECIAL_IMAGE;
  return COURSE_IMAGES[index % COURSE_IMAGES.length];
}

function isCancelled(a: BookwhenAttributes, title: string): boolean {
  if (a.cancelled === true) return true;
  if ((a.status ?? '').toLowerCase() === 'cancelled') return true;
  const tags = (a.tags ?? []).map((t) => (t ?? '').toLowerCase());
  if (tags.some((t) => t.includes('cancel'))) return true;
  const lower = title.toLowerCase();
  if (lower.includes('cancelled') || lower.includes('canceled')) return true;
  return false;
}

function mapToGolfEvent(ev: BookwhenEvent, index: number): GolfEvent | null {
  const a = ev.attributes ?? {};
  const startStr = a.start_at;
  if (!startStr) return null;
  const start = new Date(startStr);
  if (Number.isNaN(start.getTime())) return null;

  const rawTitle = (a.title ?? '').trim() || 'SER Event';
  const cancelled = isCancelled(a, rawTitle);
  const title = rawTitle.replace(/\s*\(?cancelled\)?\s*/gi, ' ').replace(/\s+/g, ' ').trim() || rawTitle;
  const type = classify(title);
  const allDay = a.all_day === true;
  const location = (a.location ?? a.location_text ?? '').trim() ||
    (title.toLowerCase().includes('killermont') ? 'Killermont' :
     title.toLowerCase().includes('gailes') ? 'Gailes' : 'Glasgow Golf Club');

  const description = stripHtml(a.details_html ?? a.details) ||
    'Join the Sunday Early Risers for this event. Tap Book Now for full details on Bookwhen.';

  return {
    id: ev.id,
    title,
    date: formatDateDisplay(start),
    dayOfWeek: getDayOfWeek(start),
    time: formatTime(start, allDay),
    location,
    description,
    type,
    spotsInfo: cancelled
      ? 'This event has been cancelled'
      : type === 'special'
        ? 'Limited places available'
        : 'Open to all SER members',
    imageUrl: pickImage(type, index, title),
    bookingUrl: `https://bookwhen.com/${ACCOUNT_SLUG}/e/${ev.id}`,
    cancelled,
  };
}

/**
 * Fetch upcoming events from Bookwhen API and map them into GolfEvent objects.
 * Sorted ascending by date.
 */
export async function fetchBookwhenEvents(): Promise<GolfEvent[]> {
  const apiKey = process.env.EXPO_PUBLIC_BOOKWHEN_API_KEY;
  if (!apiKey) {
    throw new Error('Missing EXPO_PUBLIC_BOOKWHEN_API_KEY');
  }

  const now = new Date();
  const from = `${now.getUTCFullYear()}${pad(now.getUTCMonth() + 1)}${pad(now.getUTCDate())}${pad(now.getUTCHours())}${pad(now.getUTCMinutes())}${pad(now.getUTCSeconds())}`;
  const url = `${BOOKWHEN_API_URL}?filter[from]=${from}&page[size]=100`;

  const authHeader = `Basic ${typeof btoa !== 'undefined'
    ? btoa(`${apiKey}:`)
    : Buffer.from(`${apiKey}:`).toString('base64')}`;

  const res = await fetch(url, {
    method: 'GET',
    headers: {
      Authorization: authHeader,
      Accept: 'application/vnd.api+json',
    },
  });

  if (!res.ok) {
    const body = await res.text().catch(() => '');
    throw new Error(`Bookwhen request failed: ${res.status} ${body.slice(0, 200)}`);
  }

  const json = (await res.json()) as BookwhenResponse;
  const items = json.data ?? [];

  const mapped = items
    .map((e, i) => mapToGolfEvent(e, i))
    .filter((e): e is GolfEvent => e !== null);

  mapped.sort((a, b) => {
    const da = parseDisplayDate(a.date).getTime();
    const db = parseDisplayDate(b.date).getTime();
    return da - db;
  });

  return mapped;
}

function parseDisplayDate(display: string): Date {
  const parts = display.split(' ');
  const day = parseInt(parts[0], 10);
  const monthIndex = MONTHS.indexOf(parts[1]);
  const year = parseInt(parts[2], 10);
  return new Date(year, monthIndex, day);
}
