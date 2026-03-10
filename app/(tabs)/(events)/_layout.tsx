import { Stack } from 'expo-router';
import React from 'react';
import Colors from '@/constants/colors';

export default function EventsLayout() {
  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: Colors.darkGreen },
        headerTintColor: Colors.white,
        headerTitleStyle: { fontWeight: '600' as const },
        contentStyle: { backgroundColor: Colors.backgroundDark },
      }}
    >
      <Stack.Screen
        name="index"
        options={{ title: 'Upcoming Events' }}
      />
      <Stack.Screen
        name="[eventId]"
        options={{ title: 'Event Details' }}
      />
    </Stack>
  );
}
