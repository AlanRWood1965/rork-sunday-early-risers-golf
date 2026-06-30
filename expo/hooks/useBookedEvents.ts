import { useState, useEffect, useCallback } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import createContextHook from '@nkzw/create-context-hook';

const STORAGE_KEY = 'ser_booked_events';

export const [BookedEventsProvider, useBookedEvents] = createContextHook(() => {
  const [bookedIds, setBookedIds] = useState<Set<string>>(new Set());
  const [ready, setReady] = useState(false);

  useEffect(() => {
    AsyncStorage.getItem(STORAGE_KEY)
      .then((stored) => {
        if (stored) {
          try {
            const parsed: string[] = JSON.parse(stored);
            if (Array.isArray(parsed)) {
              setBookedIds(new Set(parsed));
            }
          } catch {
            // ignore corrupt data
          }
        }
      })
      .catch(() => {})
      .finally(() => setReady(true));
  }, []);

  const persist = useCallback((ids: Set<string>) => {
    AsyncStorage.setItem(STORAGE_KEY, JSON.stringify([...ids])).catch(() => {});
  }, []);

  const addBooked = useCallback(
    (eventId: string) => {
      setBookedIds((prev) => {
        if (prev.has(eventId)) return prev;
        const next = new Set(prev);
        next.add(eventId);
        persist(next);
        return next;
      });
    },
    [persist],
  );

  const removeBooked = useCallback(
    (eventId: string) => {
      setBookedIds((prev) => {
        if (!prev.has(eventId)) return prev;
        const next = new Set(prev);
        next.delete(eventId);
        persist(next);
        return next;
      });
    },
    [persist],
  );

  const isBooked = useCallback(
    (eventId: string) => bookedIds.has(eventId),
    [bookedIds],
  );

  return { bookedIds, ready, addBooked, removeBooked, isBooked };
});
