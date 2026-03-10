# Revenant Gear Checker

A RuneLite plugin that checks whether your gear is set up correctly to safespot Revenants in the Revenant Caves.

## How it works

Revenants pick the attack style you have the **lowest defence** against. On certain tiles, they will attempt to melee you — giving you the opportunity to safespot them, since melee cannot reach you there.

The plugin checks two conditions:

- **Ranged defence bonus** must be higher than your highest melee defence stat
- **Effective magic defence** must be higher than your highest melee defence stat

When both conditions are met, the Revenant is forced into melee and the safespot works.

### Effective magic defence formula

```
Effective magic def = floor(Magic × 0.7 + Defence × 0.3) + magic defence bonus
```

## Panel

The panel updates automatically whenever you change your equipped gear. It shows:

- Your Magic and Defence levels
- Your defence bonuses (stab, slash, crush, ranged, effective magic)
- A clear **SAFE** / **NOT SAFE** status with the reason if not safe

## More information

For a full guide on the Revenant Caves safespot, including which tiles to use and recommended gear setups, visit [hcim.net](https://hcim.net/pages/guides/revenant-caves-safespots.html).
