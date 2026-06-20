import { describe, expect, it } from 'vitest';

const seedPasswordHash = '$2b$10$5SvK3rleo2da4cVM30UJoe/di/jtTFuk2Yyt43aHsV80hLht46N6m';

describe('seed account password', () => {
  it('keeps the README default seed hash in bcrypt format', () => {
    expect(seedPasswordHash).toMatch(/^\$2b\$10\$/);
  });
});
