import { useState, useEffect, useCallback, useRef } from 'react';
import { AppState } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import createContextHook from '@nkzw/create-context-hook';

const STORAGE_KEY = 'ser_booked_events';

export const [BookedEventsProvider, useBookedEvents] = createContextHook(() => {
  const [bookedIds, setBookedIds] = useState<Set<string>>(new Set());
  const [ready, setReady] = useState(false);
  const loadedRef = useRef(false);

  const loadFromStorage = useCallback(async () => {
    try {
      const stored = await AsyncStorage.getItem(STORAGE_KEY);
      if (stored) {
        const parsed: unknown = JSON.parse(stored);
        if (Array.isArray(parsed)) {
          setBookedIds(new Set(parsed.filter((v): v is string => typeof v === 'string')));
        }
      }
    } catch {
      // ignore corrupt data
    } finally {
      loadedRef.current = true;
      setReady(true);
    }
  }, []);

  // Load persisted booked IDs on mount
  useEffect(() => {
    loadFromStorage();
  }, [loadFromStorage]);

  // Reload from storage when app returns to foreground (covers cold-start + restore)
  useEffect(() => {
    const sub = AppState.addEventListener('change', (state) => {
      if (state === 'active' && loadedRef.current) {
        loadFromStorage();
      }
    });
    return () => sub.remove();
  }, [loadFromStorage]);

  const persist = useCallback(async (ids: Set<string>) => {
    try {
      await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify([...ids]));
    } catch {
      // ignore storage errors
    }
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
