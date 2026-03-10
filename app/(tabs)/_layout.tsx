import { Tabs } from 'expo-router';
import { Calendar, Info } from 'lucide-react-native';
import React from 'react';
import Colors from '@/constants/colors';

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarActiveTintColor: Colors.gold,
        tabBarInactiveTintColor: Colors.textMuted,
        tabBarStyle: {
          backgroundColor: Colors.tabBar,
          borderTopColor: Colors.tabBarBorder,
          borderTopWidth: 1,
        },
        tabBarLabelStyle: {
          fontSize: 11,
          fontWeight: '600' as const,
          letterSpacing: 0.3,
        },
      }}
    >
      <Tabs.Screen
        name="(events)"
        options={{
          title: 'Events',
          tabBarIcon: ({ color, size }) => <Calendar size={size} color={color} />,
        }}
      />
      <Tabs.Screen
        name="club"
        options={{
          title: 'The Club',
          tabBarIcon: ({ color, size }) => <Info size={size} color={color} />,
        }}
      />

    </Tabs>
  );
}
