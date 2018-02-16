/* machine/jupiter.c */

extern OPBASE_HANDLER( jupiter_opbaseoverride );
extern extern extern void jupiter_exit_ace (int id);
extern int jupiter_load_ace (int id);
extern void jupiter_exit_tap (int id);
extern int jupiter_load_tap (int id);
extern public static ReadHandlerPtr jupiter_port_fefe_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr jupiter_port_fdfe_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr jupiter_port_fbfe_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr jupiter_port_f7fe_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr jupiter_port_effe_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr jupiter_port_dffe_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr jupiter_port_bffe_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static ReadHandlerPtr jupiter_port_7ffe_r  = new ReadHandlerPtr() { public int handler(int offset);
extern public static WriteHandlerPtr jupiter_port_fe_w = new WriteHandlerPtr() {public void handler(int offset, int data);

/* vidhrdw/jupiter.c */

extern extern void jupiter_vh_stop (void);
extern void jupiter_vh_screenrefresh (struct osd_bitmap *bitmap,
												int full_refresh);
extern extern UBytePtr jupiter_charram;
extern size_t jupiter_charram_size;

/* systems/jupiter.c */

extern struct GfxLayout jupiter_charlayout;
												
