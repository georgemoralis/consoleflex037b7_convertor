/* machine/uk101.c */

extern extern extern extern extern extern extern int uk101_init_cassette(int id);
extern void uk101_exit_cassette(int id);

/* vidhrdw/uk101.c */

extern extern extern void uk101_vh_screenrefresh (struct osd_bitmap *bitmap,
												int full_refresh);
extern void superbrd_vh_screenrefresh (struct osd_bitmap *bitmap,
												int full_refresh);

