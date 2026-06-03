# Revenant Gear Checker

A RuneLite plugin that checks whether your gear is set up correctly to safespot Revenants in the Revenant Caves.

## How it works

Revenants pick the attack style you have the **lowest defence roll** against. On certain tiles, they will attempt to melee you — giving you the opportunity to safespot them, since melee cannot reach you there.

The plugin checks two conditions:

- **Ranged defence roll** must be higher than the melee defence roll
- **Magic defence roll** must be higher than the melee defence roll

When both conditions are met, the Revenant is forced into melee and the safespot works.

### Defence roll formulas

```
Melee defence roll = (Defence + 8) × (highest melee bonus + 64)
Ranged defence roll = (Defence + 8) × (ranged bonus + 64)
Magic defence roll = (magicEd + 8) × (magic bonus + 64)

magicEd = floor(Magic × 0.7 + Defence × 0.3)
```

Because magic uses a mixed effective level (70% Magic + 30% Defence), players with high Defence but low Magic can have a lower magic defence roll than their melee defence roll — even if their raw magic defence bonus looks sufficient. This is the most common cause of the safespot failing unexpectedly.

## Panel

The panel updates automatically whenever you change your equipped gear. It shows:

- Your Magic and Defence levels
- Your defence bonuses (stab, slash, crush, ranged, magic)
- The roll-adjusted magic defence value (normalised to the same scale as melee/ranged bonuses)
- A clear **SAFE** / **NOT SAFE** status with the reason if not safe

## More information

For a full interactive gear checker and guide on the Revenant Caves safespot, visit [hcim.net](https://hcim.net/pages/guides/rev-safespot-checker.html).
