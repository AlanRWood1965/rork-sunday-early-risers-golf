import React, { useMemo, useRef, useCallback, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Animated,
  Platform,
  Linking,
} from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { Image } from 'expo-image';
import * as Haptics from 'expo-haptics';
import * as WebBrowser from 'expo-web-browser';
import {
  Calendar,
  MapPin,
  Clock,
  ExternalLink,
  Users,
  Star,
} from 'lucide-react-native';
import Colors from '@/constants/colors';
import { getEventById, FALLBACK_IMAGE } from '@/constants/events';

export default function EventDetailScreen() {
  const { eventId } = useLocalSearchParams<{ eventId: string }>();
  const event = useMemo(() => getEventById(eventId ?? ''), [eventId]);
  const btnScale = useRef(new Animated.Value(1)).current;
  const [imageError, setImageError] = useState(false);

  const handleBook = useCallback(async () => {
    if (!event) return;
    if (Platform.OS !== 'web') {
      Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium);
    }
    try {
      if (Platform.OS === 'web') {
        Linking.openURL(event.bookingUrl);
      } else {
        await WebBrowser.openBrowserAsync(event.bookingUrl);
      }
    } catch (err) {
      console.log('Error opening booking URL:', err);
      Linking.openURL(event.bookingUrl);
    }
  }, [event]);

  const handleBtnPressIn = useCallback(() => {
    Animated.spring(btnScale, {
      toValue: 0.95,
      useNativeDriver: true,
    }).start();
  }, [btnScale]);

  const handleBtnPressOut = useCallback(() => {
    Animated.spring(btnScale, {
      toValue: 1,
      friction: 3,
      useNativeDriver: true,
    }).start();
  }, [btnScale]);

  if (!event) {
    return (
      <View style={styles.container}>
        <View style={styles.errorContainer}>
          <Text style={styles.errorText}>Event not found</Text>
        </View>
      </View>
    );
  }

  const isSpecial = event.type === 'special';

  return (
    <View style={styles.container}>
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.imageContainer}>
          <Image
            source={{ uri: imageError ? FALLBACK_IMAGE : event.imageUrl }}
            style={styles.heroImage}
            contentFit="cover"
            transition={400}
            onError={() => setImageError(true)}
          />
          <View style={styles.imageGradient} />
          {isSpecial && (
            <View style={styles.specialBadge}>
              <Star
                size={14}
                color={Colors.backgroundDark}
                fill={Colors.gold}
              />
              <Text style={styles.specialBadgeText}>Special Event</Text>
            </View>
          )}
        </View>

        <View style={styles.body}>
          <Text style={styles.title}>{event.title}</Text>

          <View style={styles.infoCards}>
            <View style={styles.infoCard}>
              <Calendar size={20} color={Colors.gold} />
              <View>
                <Text style={styles.infoLabel}>Date</Text>
                <Text style={styles.infoValue}>
                  {event.dayOfWeek}, {event.date}
                </Text>
              </View>
            </View>

            <View style={styles.infoCard}>
              <Clock size={20} color={Colors.gold} />
              <View>
                <Text style={styles.infoLabel}>Time</Text>
                <Text style={styles.infoValue}>{event.time}</Text>
              </View>
            </View>

            <View style={styles.infoCard}>
              <MapPin size={20} color={Colors.gold} />
              <View>
                <Text style={styles.infoLabel}>Location</Text>
                <Text style={styles.infoValue}>{event.location}</Text>
              </View>
            </View>

            <View style={styles.infoCard}>
              <Users size={20} color={Colors.gold} />
              <View>
                <Text style={styles.infoLabel}>Availability</Text>
                <Text style={styles.infoValue}>{event.spotsInfo}</Text>
              </View>
            </View>
          </View>

          <View style={styles.descriptionSection}>
            <Text style={styles.descriptionLabel}>ABOUT THIS EVENT</Text>
            <Text style={styles.description}>{event.description}</Text>
          </View>
        </View>
      </ScrollView>

      <View style={styles.bookingBar}>
        <View style={styles.bookingInfo}>
          <Text style={styles.bookingLocation}>{event.location}</Text>
          <Text style={styles.bookingDate}>{event.date}</Text>
        </View>
        <Animated.View style={{ transform: [{ scale: btnScale }] }}>
          <TouchableOpacity
            style={styles.bookButton}
            onPress={handleBook}
            onPressIn={handleBtnPressIn}
            onPressOut={handleBtnPressOut}
            activeOpacity={1}
            testID="book-now-button"
          >
            <Text style={styles.bookButtonText}>Book Now</Text>
            <ExternalLink size={16} color={Colors.backgroundDark} />
          </TouchableOpacity>
        </Animated.View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.backgroundDark,
  },
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    paddingBottom: 100,
  },
  errorContainer: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  errorText: {
    color: Colors.textMuted,
    fontSize: 16,
  },
  imageContainer: {
    height: 260,
    position: 'relative',
  },
  heroImage: {
    width: '100%',
    height: '100%',
  },
  imageGradient: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(10, 20, 10, 0.35)',
  },
  specialBadge: {
    position: 'absolute',
    top: 16,
    right: 16,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    backgroundColor: Colors.gold,
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 14,
  },
  specialBadgeText: {
    color: Colors.backgroundDark,
    fontSize: 12,
    fontWeight: '700' as const,
    letterSpacing: 0.5,
  },
  body: {
    padding: 20,
    gap: 20,
  },
  title: {
    color: Colors.white,
    fontSize: 24,
    fontWeight: '800' as const,
    letterSpacing: 0.3,
    lineHeight: 30,
  },
  infoCards: {
    gap: 10,
  },
  infoCard: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 14,
    backgroundColor: Colors.cardGreen,
    padding: 14,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: Colors.cardBorder,
  },
  infoLabel: {
    color: Colors.textMuted,
    fontSize: 11,
    fontWeight: '600' as const,
    letterSpacing: 0.5,
    textTransform: 'uppercase',
  },
  infoValue: {
    color: Colors.textPrimary,
    fontSize: 15,
    fontWeight: '500' as const,
    marginTop: 1,
  },
  descriptionSection: {
    gap: 10,
  },
  descriptionLabel: {
    color: Colors.gold,
    fontSize: 11,
    fontWeight: '700' as const,
    letterSpacing: 2,
  },
  description: {
    color: Colors.textSecondary,
    fontSize: 15,
    lineHeight: 23,
  },
  bookingBar: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: Colors.darkGreen,
    borderTopWidth: 1,
    borderTopColor: Colors.cardBorder,
    paddingHorizontal: 20,
    paddingVertical: 14,
    paddingBottom: 30,
  },
  bookingInfo: {
    flex: 1,
  },
  bookingLocation: {
    color: Colors.white,
    fontSize: 15,
    fontWeight: '600' as const,
  },
  bookingDate: {
    color: Colors.textMuted,
    fontSize: 13,
    marginTop: 2,
  },
  bookButton: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    backgroundColor: Colors.gold,
    paddingHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 24,
  },
  bookButtonText: {
    color: Colors.backgroundDark,
    fontSize: 15,
    fontWeight: '700' as const,
  },
});
