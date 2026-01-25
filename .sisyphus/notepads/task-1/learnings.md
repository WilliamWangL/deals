
## Brand System Update (Honey-like Palette)
- **Palette**: Adopted a warm "Honey" palette with Gold/Amber (`oklch(0.68 0.16 75)`) as primary, Warm Sand (`oklch(0.96 0.02 85)`) as secondary, and Dark Warm Charcoal (`oklch(0.20 0.02 85)`) as foreground.
- **Typography**: Maintained `Plus Jakarta Sans` and `Space Grotesk` but refined usage in components.
- **Components**:
    - `Button`: Updated to `rounded-xl` with soft shadows and hover lift effects.
    - `Badge`: Added semantic variants (`deal`, `savings`, `featured`, `exclusive`) with gradients.
    - `Card`: Softened to `rounded-2xl` with subtle borders and shadows.
    - `Input`: Updated focus rings to match the new primary gold.
    - `Sheet` & `Dropdown`: Aligned with new radius and shadow tokens.
- **Tailwind v4**: Leveraged `@theme` block in `globals.css` for token definitions.
