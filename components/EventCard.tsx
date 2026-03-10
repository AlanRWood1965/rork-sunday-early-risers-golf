import React, { useRef, useCallback, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Animated,
} from 'react-native';
import { Image } from 'expo-image';
import { Calendar, MapPin, Clock, Star } from 'lucide-react-native';
import Colors from '@/constants/colors';
import { GolfEvent, FALLBACK_IMAGE } from '@/constants/events';

interface EventCardProps {
  event: GolfEvent;
  onPress: (event: GolfEvent) => void;
  index?: number;
}

export default React.memo(function EventCard({
  event,
  onPress,
  index = 0,
}: EventCardProps) {
  const scaleAnim = useRef(new Animated.Value(1)).current;
  const [imageError, setImageError] = useState(false);

  const handlePressIn = useCallback(() => {
    Animated.spring(scaleAnim, {
      toValue: 0.97,
      useNativeDriver: true,
    }).start();
  }, [scaleAnim]);

  const handlePressOut = useCallback(() => {
    Animated.spring(scaleAnim, {
      toValue: 1,
      friction: 3,
      useNativeDriver: true,
    }).start();
  }, [scaleAnim]);

  const isSpecial = event.type === 'special';

  return (
    <Animated.View
      style={[styles.cardWrapper, { transform: [{ scale: scaleAnim }] }]}
    >
      <TouchableOpacity
        style={[styles.card, isSpecial && styles.specialCard]}
        onPress={() => onPress(event)}
        onPressIn={handlePressIn}
        onPressOut={handlePressOut}
        activeOpacity={1}
        testID={`event-card-${event.id}`}
      >
        <Image
          source={{ uri: imageError ? FALLBACK_IMAGE : event.imageUrl }}
          style={styles.image}
          contentFit="cover"
          transition={300}
          onError={() => setImageError(true)}
        />
        <View style={styles.imageOverlay} />

        {isSpecial && (
          <View style={styles.specialBadge}>
            <Star size={12} color={Colors.backgroundDark} fill={Colors.gold} />
            <Text style={styles.specialBadgeText}>Special Event</Text>
          </View>
        )}

        <View style={styles.content}>
          <Text style={styles.title} numberOfLines={2}>
            {event.title}
          </Text>

          <View style={styles.detailsRow}>
            <View style={styles.detail}>
              <Calendar size={14} color={Colors.gold} />
              <Text style={styles.detailText}>
                {event.dayOfWeek}, {event.date}
              </Text>
            </View>
          </View>

          <View style={styles.detailsRow}>
            <View style={styles.detail}>
              <MapPin size={14} color={Colors.gold} />
              <Text style={styles.detailText}>{event.location}</Text>
            </View>
            <View style={styles.detail}>
              <Clock size={14} color={Colors.gold} />
              <Text style={styles.detailText}>{event.time}</Text>
            </View>
          </View>

          <View style={styles.footer}>
            <Text style={styles.spotsText}>{event.spotsInfo}</Text>
            <View style={styles.bookBtn}>
              <Text style={styles.bookBtnText}>Book</Text>
            </View>
          </View>
        </View>
      </TouchableOpacity>
    </Animated.View>
  );
});

const styles = StyleSheet.create({
  cardWrapper: {
    marginHorizontal: 16,
    marginBottom: 14,
  },
  card: {
    backgroundColor: Colors.cardGreen,
    borderRadius: 16,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: Colors.cardBorder,
  },
  specialCard: {
    borderColor: Colors.goldDark,
    borderWidth: 1.5,
  },
  image: {
    width: '100%',
    height: 160,
  },
  imageOverlay: {
    ...StyleSheet.absoluteFillObject,
    height: 160,
    backgroundColor: 'rgba(10, 20, 10, 0.3)',
  },
  specialBadge: {
    position: 'absolute',
    top: 12,
    right: 12,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
    backgroundColor: Colors.gold,
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 12,
  },
  specialBadgeText: {
    color: Colors.backgroundDark,
    fontSize: 11,
    fontWeight: '700' as const,
    letterSpacing: 0.5,
  },
  content: {
    padding: 14,
    gap: 8,
  },
  title: {
    color: Colors.white,
    fontSize: 17,
    fontWeight: '700' as const,
    letterSpacing: 0.2,
  },
  detailsRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 16,
  },
  detail: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  detailText: {
    color: Colors.textSecondary,
    fontSize: 13,
  },
  footer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginTop: 4,
  },
  spotsText: {
    color: Colors.textMuted,
    fontSize: 12,
    fontStyle: 'italic',
  },
  bookBtn: {
    backgroundColor: Colors.gold,
    paddingHorizontal: 18,
    paddingVertical: 7,
    borderRadius: 20,
  },
  bookBtnText: {
    color: Colors.backgroundDark,
    fontSize: 13,
    fontWeight: '700' as const,
  },
});
