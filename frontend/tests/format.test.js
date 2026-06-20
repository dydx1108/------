import { describe, expect, it } from 'vitest';
import { formatDate, pickHeroImage, statusMap, typeMap } from '../src/utils/format';

describe('format utils', () => {
  it('maps item status and type labels for the UI', () => {
    expect(statusMap.pending).toBe('待审核');
    expect(statusMap.published).toBe('已发布');
    expect(typeMap.lost).toBe('失物');
    expect(typeMap.found).toBe('拾物');
  });

  it('formats database date values as yyyy-mm-dd', () => {
    expect(formatDate('2026-06-14T12:00:00.000Z')).toBe('2026-06-14');
    expect(formatDate('')).toBe('-');
  });

  it('uses the first available item image before the generated hero fallback', () => {
    expect(pickHeroImage([{ image_url: '' }, { image_url: '/uploads/items/a.png' }])).toBe('/uploads/items/a.png');
    expect(pickHeroImage([])).toBe('/generated/hero-campus.svg');
  });
});
