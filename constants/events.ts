export interface GolfEvent {
  id: string;
  title: string;
  date: string;
  dayOfWeek: string;
  time: string;
  location: string;
  description: string;
  type: 'weekly' | 'special';
  spotsInfo: string;
  imageUrl: string;
  bookingUrl: string;
}

const COURSE_IMAGES = [
  'https://images.unsplash.com/photo-1587174486073-ae5e5cff23aa?w=800&q=80',
  'https://images.unsplash.com/photo-1535131749006-b7f58c99034b?w=800&q=80',
  'https://images.unsplash.com/photo-1592919505780-303950717480?w=800&q=80',
  'https://images.unsplash.com/photo-1600005082646-28a8e9e1f498?w=800&q=80',
  'https://images.unsplash.com/photo-1593111774240-d529f12cf4bb?w=800&q=80',
  'https://images.unsplash.com/photo-1622396636133-b43e6f94c42a?w=800&q=80',
  'https://images.unsplash.com/photo-1580126755789-85101eba6306?w=800&q=80',
];

export const FALLBACK_IMAGE = 'https://images.unsplash.com/photo-1587174486073-ae5e5cff23aa?w=800&q=80';

const MONTHS = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December',
];

function pad(n: number): string {
  return n < 10 ? `0${n}` : `${n}`;
}

function generateSundayMatches(): GolfEvent[] {
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const endDate = new Date(today);
  endDate.setMonth(endDate.getMonth() + 4);

  const current = new Date(today);
  const dayOfWeek = current.getDay();
  const daysUntilSunday = dayOfWeek === 0 ? 0 : 7 - dayOfWeek;
  current.setDate(current.getDate() + daysUntilSunday);

  const sundays: { iso: string; display: string; compact: string }[] = [];
  while (current <= endDate) {
    const y = current.getFullYear();
    const m = current.getMonth();
    const d = current.getDate();
    sundays.push({
      iso: `${y}-${pad(m + 1)}-${pad(d)}`,
      display: `${d} ${MONTHS[m]} ${y}`,
      compact: `${y}${pad(m + 1)}${pad(d)}`,
    });
    current.setDate(current.getDate() + 7);
  }

  return sundays.map((s, i) => ({
    id: `sunday-${s.iso}`,
    title: 'Sunday Morning Killermont Match',
    date: s.display,
    dayOfWeek: 'Sunday',
    time: 'Early Morning Tee-off',
    location: 'Killermont',
    description:
      'Join the Sunday Early Risers for a weekly match at the beautiful Killermont course. Enjoy breakfast and a competitive yet friendly round of golf followed by refreshments in the clubhouse.',
    type: 'weekly' as const,
    spotsInfo: 'Open to all SER members',
    imageUrl: COURSE_IMAGES[i % COURSE_IMAGES.length],
    bookingUrl: `https://bookwhen.com/ser-golf/e/ev-sk18-${s.compact}000000`,
  }));
}

const SPECIAL_EVENTS: GolfEvent[] = [
  {
    id: 'spring-meeting-2026',
    title: "SER's Spring Meeting to Gailes 2026",
    date: '24 April 2026',
    dayOfWeek: 'Friday',
    time: 'Full Day Event',
    location: 'Gailes',
    description:
      "The annual Spring Meeting outing to the renowned Gailes Links. A full day of golf at one of Scotland's finest courses. This is always a highlight of the SER calendar — don't miss out!",
    type: 'special',
    spotsInfo: 'Limited places available',
    imageUrl:
      'https://images.unsplash.com/photo-1611374243147-44a702c2d44c?w=800&q=80',
    bookingUrl: 'https://bookwhen.com/ser-golf/e/ev-suc7i-20260424000000',
  },
];

function parseEventDate(display: string): Date {
  const parts = display.split(' ');
  const day = parseInt(parts[0], 10);
  const monthIndex = MONTHS.indexOf(parts[1]);
  const year = parseInt(parts[2], 10);
  return new Date(year, monthIndex, day);
}

function filterWithinWindow(events: GolfEvent[]): GolfEvent[] {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const endDate = new Date(today);
  endDate.setMonth(endDate.getMonth() + 4);

  return events.filter((e) => {
    const d = parseEventDate(e.date);
    return d >= today && d <= endDate;
  });
}

function buildEvents(): GolfEvent[] {
  return [
    ...generateSundayMatches(),
    ...filterWithinWindow(SPECIAL_EVENTS),
  ].sort((a, b) => parseEventDate(a.date).getTime() - parseEventDate(b.date).getTime());
}

export function getUpcomingEvents(): GolfEvent[] {
  return buildEvents();
}

export function getEventById(id: string): GolfEvent | undefined {
  return buildEvents().find((e) => e.id === id);
}

export function getSpecialEvents(): GolfEvent[] {
  return buildEvents().filter((e) => e.type === 'special');
}

export function getWeeklyEvents(): GolfEvent[] {
  return buildEvents().filter((e) => e.type === 'weekly');
}

export const EVENTS = buildEvents();
