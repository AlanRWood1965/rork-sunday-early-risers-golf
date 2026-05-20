import { useQuery } from '@tanstack/react-query';
import { fetchBookwhenEvents } from '@/services/bookwhen';
import { GolfEvent } from '@/constants/events';

const EVENTS_QUERY_KEY = ['bookwhen-events'] as const;

export function useEvents() {
  return useQuery<GolfEvent[], Error>({
    queryKey: EVENTS_QUERY_KEY,
    queryFn: fetchBookwhenEvents,
    staleTime: 5 * 60 * 1000,
    refetchOnWindowFocus: true,
  });
}

export function useWeeklyEvents() {
  const q = useEvents();
  return {
    ...q,
    data: q.data?.filter((e) => e.type === 'weekly') ?? [],
  };
}

export function useSpecialEvents() {
  const q = useEvents();
  return {
    ...q,
    data: q.data?.filter((e) => e.type === 'special') ?? [],
  };
}

export function useEventById(id: string | undefined) {
  const q = useEvents();
  return {
    ...q,
    data: id ? q.data?.find((e) => e.id === id) : undefined,
  };
}
