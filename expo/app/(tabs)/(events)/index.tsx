import React, { useMemo, useState, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  ActivityIndicator,
  RefreshControl,
  Linking,
  Platform,
} from 'react-native';
import { useRouter } from 'expo-router';
import { Image } from 'expo-image';
import * as Haptics from 'expo-haptics';
import * as WebBrowser from 'expo-web-browser';
import { UserCircle } from 'lucide-react-native';
import Colors from '@/constants/colors';
import { GolfEvent } from '@/constants/events';
import { useEvents } from '@/hooks/useEvents';
import EventCard from '@/components/EventCard';

type FilterType = 'all' | 'weekly' | 'special';

const CUSTOMER_PORTAL_URL = 'https://my.bookwhen.com';

export default function EventsScreen() {
  const router = useRouter();
  const [filter, setFilter] = useState<FilterType>('all');
  const { data: allEvents, isLoading, isError, error, refetch, isRefetching } = useEvents();

  const openCustomerPortal = useCallback(async () => {
    if (Platform.OS !== 'web') {
      Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
    }
    try {
      if (Platform.OS === 'web') {
        Linking.openURL(CUSTOMER_PORTAL_URL);
      } else {
        await WebBrowser.openBrowserAsync(CUSTOMER_PORTAL_URL);
      }
    } catch {
      Linking.openURL(CUSTOMER_PORTAL_URL);
    }
  }, []);

  const events = useMemo(() => {
    const list = allEvents ?? [];
    switch (filter) {
      case 'weekly':
        return list.filter((e) => e.type === 'weekly');
      case 'special':
        return list.filter((e) => e.type === 'special');
      default:
        return list;
    }
  }, [filter, allEvents]);

  const specialEvents = useMemo(
    () => (allEvents ?? []).filter((e) => e.type === 'special'),
    [allEvents]
  );

  const handleEventPress = useCallback(
    (event: GolfEvent) => {
      if (Platform.OS !== 'web') {
        Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
      }
      router.push(`/(events)/${event.id}` as any);
    },
    [router]
  );

  const handleFilterChange = useCallback((f: FilterType) => {
    setFilter(f);
    if (Platform.OS !== 'web') {
      Haptics.selectionAsync();
    }
  }, []);

  const renderHeader = useCallback(
    () => (
      <View style={styles.header}>
        <View style={styles.heroSection}>
          <Image
            source={{
              uri: 'https://images.unsplash.com/photo-1587174486073-ae5e5cff23aa?w=1200&q=80',
            }}
            style={styles.heroImage}
            contentFit="cover"
            transition={500}
          />
          <View style={styles.heroOverlay} />
          <View style={styles.heroContent}>
            <Image
              source={{ uri: 'https://glasgowgolfclub.com/wp-content/uploads/2025/08/Glasgow-Golf-Club-Logo-Flat-White.png' }}
              style={styles.clubLogo}
              contentFit="contain"
              transition={400}
            />
            <Text style={styles.heroSubtitle}>SUNDAY EARLY RISERS</Text>
            <Text style={styles.heroTitle}>Glasgow Golf Club</Text>
            <Text style={styles.heroLocation}>Killermont</Text>
          </View>

          <TouchableOpacity
            style={styles.myBookingsBtn}
            onPress={openCustomerPortal}
            activeOpacity={0.8}
            testID="customer-portal-link"
          >
            <UserCircle size={16} color={Colors.gold} />
            <Text style={styles.myBookingsBtnText}>My Bookings</Text>
          </TouchableOpacity>
        </View>

        {specialEvents.length > 0 && (
          <View style={styles.featuredSection}>
            <Text style={styles.sectionLabel}>FEATURED</Text>
            {specialEvents.map((event) => (
              <EventCard
                key={event.id}
                event={event}
                onPress={handleEventPress}
              />
            ))}
          </View>
        )}

        <View style={styles.filterRow}>
          <Text style={styles.sectionLabel}>UPCOMING MATCHES</Text>
          <View style={styles.filters}>
            {(['all', 'weekly', 'special'] as FilterType[]).map((f) => (
              <TouchableOpacity
                key={f}
                style={[styles.filterBtn, filter === f && styles.filterBtnActive]}
                onPress={() => handleFilterChange(f)}
              >
                <Text
                  style={[
                    styles.filterText,
                    filter === f && styles.filterTextActive,
                  ]}
                >
                  {f === 'all' ? 'All' : f === 'weekly' ? 'Weekly' : 'Special'}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>
      </View>
    ),
    [filter, specialEvents, handleEventPress, handleFilterChange]
  );

  const renderItem = useCallback(
    ({ item, index }: { item: GolfEvent; index: number }) => (
      <EventCard event={item} onPress={handleEventPress} index={index} />
    ),
    [handleEventPress]
  );

  const keyExtractor = useCallback((item: GolfEvent) => item.id, []);

  return (
    <View style={styles.container}>
      <FlatList
        data={events}
        renderItem={renderItem}
        keyExtractor={keyExtractor}
        ListHeaderComponent={renderHeader}
        contentContainerStyle={styles.listContent}
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl
            refreshing={isRefetching}
            onRefresh={refetch}
            tintColor={Colors.gold}
          />
        }
        ListEmptyComponent={
          isLoading ? (
            <View style={styles.empty}>
              <ActivityIndicator color={Colors.gold} />
              <Text style={styles.emptyText}>Loading events…</Text>
            </View>
          ) : isError ? (
            <View style={styles.empty}>
              <Text style={styles.emptyText}>Couldn't load events</Text>
              <Text style={styles.emptySubText}>
                {error?.message ?? 'Please try again later.'}
              </Text>
              <TouchableOpacity style={styles.retryBtn} onPress={() => refetch()}>
                <Text style={styles.retryBtnText}>Retry</Text>
              </TouchableOpacity>
            </View>
          ) : (
            <View style={styles.empty}>
              <Text style={styles.emptyText}>No upcoming events</Text>
            </View>
          )
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.backgroundDark,
  },
  listContent: {
    paddingBottom: 24,
  },
  header: {
    marginBottom: 8,
  },
  heroSection: {
    height: 260,
    position: 'relative',
  },
  heroImage: {
    width: '100%',
    height: '100%',
  },
  heroOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(10, 20, 10, 0.55)',
  },
  heroContent: {
    position: 'absolute',
    bottom: 24,
    left: 20,
    right: 20,
  },
  clubLogo: {
    width: 64,
    height: 64,
    marginBottom: 10,
  },
  heroSubtitle: {
    color: Colors.gold,
    fontSize: 12,
    fontWeight: '700' as const,
    letterSpacing: 2.5,
    marginBottom: 4,
  },
  heroTitle: {
    color: Colors.white,
    fontSize: 28,
    fontWeight: '800' as const,
    letterSpacing: 0.3,
  },
  heroLocation: {
    color: Colors.textSecondary,
    fontSize: 14,
    marginTop: 2,
    letterSpacing: 0.5,
  },
  myBookingsBtn: {
    position: 'absolute',
    top: 12,
    right: 12,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    backgroundColor: 'rgba(10, 20, 10, 0.85)',
    paddingHorizontal: 12,
    paddingVertical: 7,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: Colors.goldDark,
  },
  myBookingsBtnText: {
    color: Colors.gold,
    fontSize: 12,
    fontWeight: '600' as const,
  },
  featuredSection: {
    marginTop: 20,
  },
  sectionLabel: {
    color: Colors.gold,
    fontSize: 11,
    fontWeight: '700' as const,
    letterSpacing: 2,
    marginHorizontal: 16,
    marginBottom: 12,
  },
  filterRow: {
    marginTop: 8,
  },
  filters: {
    flexDirection: 'row',
    marginHorizontal: 16,
    gap: 8,
    marginBottom: 12,
  },
  filterBtn: {
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderRadius: 16,
    backgroundColor: Colors.cardGreen,
    borderWidth: 1,
    borderColor: Colors.cardBorder,
  },
  filterBtnActive: {
    backgroundColor: Colors.gold,
    borderColor: Colors.gold,
  },
  filterText: {
    color: Colors.textSecondary,
    fontSize: 12,
    fontWeight: '600' as const,
  },
  filterTextActive: {
    color: Colors.backgroundDark,
  },
  empty: {
    alignItems: 'center',
    paddingVertical: 40,
    gap: 10,
    paddingHorizontal: 24,
  },
  emptyText: {
    color: Colors.textMuted,
    fontSize: 15,
    textAlign: 'center',
  },
  emptySubText: {
    color: Colors.textMuted,
    fontSize: 12,
    textAlign: 'center',
    opacity: 0.7,
  },
  retryBtn: {
    marginTop: 8,
    paddingHorizontal: 20,
    paddingVertical: 9,
    borderRadius: 20,
    backgroundColor: Colors.gold,
  },
  retryBtnText: {
    color: Colors.backgroundDark,
    fontSize: 13,
    fontWeight: '700' as const,
  },
});
