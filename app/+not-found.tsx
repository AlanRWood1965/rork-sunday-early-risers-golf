import { Link, Stack } from 'expo-router';
import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import Colors from '@/constants/colors';

export default function NotFoundScreen() {
  return (
    <>
      <Stack.Screen options={{ title: 'Not Found' }} />
      <View style={styles.container}>
        <Text style={styles.title}>Page not found</Text>
        <Link href="/" style={styles.link}>
          <Text style={styles.linkText}>Go back to Events</Text>
        </Link>
      </View>
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: Colors.backgroundDark,
    gap: 16,
  },
  title: {
    fontSize: 18,
    fontWeight: '600' as const,
    color: Colors.white,
  },
  link: {
    paddingVertical: 8,
    paddingHorizontal: 16,
  },
  linkText: {
    color: Colors.gold,
    fontSize: 15,
  },
});
