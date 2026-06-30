import React, { useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Platform,
  Linking,
} from 'react-native';
import { Image } from 'expo-image';
import * as WebBrowser from 'expo-web-browser';
import * as Haptics from 'expo-haptics';
import {
  Globe,
  ExternalLink,
  MapPin,
  History,
  Users,
  Trophy,
  UserCircle,
} from 'lucide-react-native';
import Colors from '@/constants/colors';

const CLUB_URL = 'https://www.glasgowgolfclub.com';
const BOOKING_URL = 'https://bookwhen.com/ser-golf';
const CUSTOMER_PORTAL_URL = 'https://bookwhen.com/ser-golf';

export default function ClubScreen() {
  const openUrl = useCallback(async (url: string) => {
    if (Platform.OS !== 'web') {
      Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
    }
    try {
      if (Platform.OS === 'web') {
        Linking.openURL(url);
      } else {
        await WebBrowser.openBrowserAsync(url);
      }
    } catch (err) {
      console.log('Error opening URL:', err);
      Linking.openURL(url);
    }
  }, []);

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
      showsVerticalScrollIndicator={false}
    >
      <View style={styles.heroSection}>
        <Image
          source={{
            uri: 'https://images.unsplash.com/photo-1535131749006-b7f58c99034b?w=1200&q=80',
          }}
          style={styles.heroImage}
          contentFit="cover"
          transition={500}
        />
        <View style={styles.heroOverlay} />
        <View style={styles.heroContent}>
          <Image
            source={{
              uri: 'https://glasgowgolfclub.com/wp-content/uploads/2025/08/Glasgow-Golf-Club-Logo-Flat-White.png',
            }}
            style={styles.clubLogo}
            contentFit="contain"
            transition={300}
          />
          <Text style={styles.heroTitle}>Glasgow Golf Club</Text>
          <Text style={styles.heroSubtitle}>Killermont, Glasgow</Text>
        </View>
      </View>

      <View style={styles.serSection}>
        <View style={styles.serBadge}>
          <Trophy size={18} color={Colors.gold} />
          <Text style={styles.serBadgeText}>SUNDAY EARLY RISERS</Text>
        </View>
        <Text style={styles.serDescription}>
          The Sunday Early Risers (SER's) are a friendly group of golfers who meet every Sunday morning at the beautiful Glasgow Golf Club Killermont course for breakfast and a competitive yet social round of golf.
        </Text>
      </View>

      <View style={styles.infoSection}>
        <Text style={styles.sectionTitle}>ABOUT THE CLUB</Text>

        <View style={styles.infoCard}>
          <View style={styles.infoCardIcon}>
            <History size={22} color={Colors.gold} />
          </View>
          <View style={styles.infoCardContent}>
            <Text style={styles.infoCardTitle}>Rich Heritage</Text>
            <Text style={styles.infoCardText}>
              Glasgow Golf Club is the ninth oldest golf club in the world. Founded in 1787, the club operates both the historic Killermont parkland course and the prestigious Gailes Links, which has been chosen by the R&A for Open Championship final qualifying in 2027.
            </Text>
          </View>
        </View>

        <View style={styles.infoCard}>
          <View style={styles.infoCardIcon}>
            <MapPin size={22} color={Colors.gold} />
          </View>
          <View style={styles.infoCardContent}>
            <Text style={styles.infoCardTitle}>Killermont Course</Text>
            <Text style={styles.infoCardText}>
              Set in the beautiful surroundings of Bearsden, the Glasgow Golf Club Killermont course offers a challenging yet enjoyable parkland experience and a stunning clubhouse.
            </Text>
          </View>
        </View>

      </View>

      <View style={styles.linksSection}>
        <Text style={styles.sectionTitle}>QUICK LINKS</Text>

        <TouchableOpacity
          style={[styles.linkCard, styles.portalCard]}
          onPress={() => openUrl(CUSTOMER_PORTAL_URL)}
          testID="customer-portal-link"
        >
          <View style={styles.portalIconWrap}>
            <UserCircle size={24} color={Colors.gold} />
          </View>
          <View style={styles.linkContent}>
            <Text style={styles.portalTitle}>My Bookings</Text>
            <Text style={styles.portalSubtitle}>
              View and manage your bookings via the Bookwhen Customer Portal
            </Text>
          </View>
          <ExternalLink size={18} color={Colors.gold} />
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.linkCard}
          onPress={() => openUrl(CLUB_URL)}
          testID="club-website-link"
        >
          <Globe size={20} color={Colors.gold} />
          <View style={styles.linkContent}>
            <Text style={styles.linkTitle}>Club Website</Text>
            <Text style={styles.linkUrl}>glasgowgolfclub.com</Text>
          </View>
          <ExternalLink size={16} color={Colors.textMuted} />
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.linkCard}
          onPress={() => openUrl(BOOKING_URL)}
          testID="booking-link"
        >
          <Trophy size={20} color={Colors.gold} />
          <View style={styles.linkContent}>
            <Text style={styles.linkTitle}>Book an Event</Text>
            <Text style={styles.linkUrl}>bookwhen.com/ser-golf</Text>
          </View>
          <ExternalLink size={16} color={Colors.textMuted} />
        </TouchableOpacity>
      </View>

      <View style={styles.footerSection}>
        <Text style={styles.footerText}>
          © 2026 Alan Wood
        </Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.backgroundDark,
  },
  content: {
    paddingBottom: 32,
  },
  heroSection: {
    height: 240,
    position: 'relative',
  },
  heroImage: {
    width: '100%',
    height: '100%',
  },
  heroOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(10, 20, 10, 0.6)',
  },
  heroContent: {
    position: 'absolute',
    bottom: 24,
    left: 0,
    right: 0,
    alignItems: 'center',
  },
  clubLogo: {
    width: 60,
    height: 60,
    marginBottom: 8,
  },
  heroTitle: {
    color: Colors.white,
    fontSize: 26,
    fontWeight: '800' as const,
    letterSpacing: 0.3,
    textAlign: 'center',
  },
  heroSubtitle: {
    color: Colors.textSecondary,
    fontSize: 14,
    marginTop: 2,
    letterSpacing: 0.5,
  },
  serSection: {
    margin: 16,
    padding: 18,
    backgroundColor: Colors.cardGreen,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: Colors.goldDark,
    gap: 12,
  },
  serBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  serBadgeText: {
    color: Colors.gold,
    fontSize: 13,
    fontWeight: '700' as const,
    letterSpacing: 2,
  },
  serDescription: {
    color: Colors.textSecondary,
    fontSize: 14,
    lineHeight: 22,
  },
  infoSection: {
    paddingHorizontal: 16,
    gap: 12,
    marginTop: 4,
  },
  sectionTitle: {
    color: Colors.gold,
    fontSize: 11,
    fontWeight: '700' as const,
    letterSpacing: 2,
    marginBottom: 4,
  },
  infoCard: {
    flexDirection: 'row',
    backgroundColor: Colors.cardGreen,
    borderRadius: 14,
    padding: 16,
    gap: 14,
    borderWidth: 1,
    borderColor: Colors.cardBorder,
  },
  infoCardIcon: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: Colors.backgroundDark,
    alignItems: 'center',
    justifyContent: 'center',
  },
  infoCardContent: {
    flex: 1,
    gap: 4,
  },
  infoCardTitle: {
    color: Colors.white,
    fontSize: 15,
    fontWeight: '700' as const,
  },
  infoCardText: {
    color: Colors.textSecondary,
    fontSize: 13,
    lineHeight: 20,
  },
  linksSection: {
    paddingHorizontal: 16,
    gap: 10,
    marginTop: 24,
  },
  linkCard: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 14,
    backgroundColor: Colors.cardGreen,
    padding: 16,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: Colors.cardBorder,
  },
  linkContent: {
    flex: 1,
  },
  linkTitle: {
    color: Colors.white,
    fontSize: 15,
    fontWeight: '600' as const,
  },
  linkUrl: {
    color: Colors.textMuted,
    fontSize: 12,
    marginTop: 2,
  },
  footerSection: {
    alignItems: 'center',
    marginTop: 32,
    gap: 4,
  },
  footerText: {
    color: Colors.textMuted,
    fontSize: 12,
    fontWeight: '500' as const,
  },
  footerSubtext: {
    color: Colors.textMuted,
    fontSize: 11,
    fontStyle: 'italic',
  },
  portalCard: {
    borderColor: Colors.goldDark,
    borderWidth: 1.5,
    backgroundColor: '#1a201a',
  },
  portalIconWrap: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: Colors.backgroundDark,
    alignItems: 'center',
    justifyContent: 'center',
  },
  portalTitle: {
    color: Colors.gold,
    fontSize: 16,
    fontWeight: '700' as const,
  },
  portalSubtitle: {
    color: Colors.textSecondary,
    fontSize: 12,
    marginTop: 2,
    lineHeight: 17,
  },
});
