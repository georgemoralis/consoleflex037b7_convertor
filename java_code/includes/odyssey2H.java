/* machine/odyssey2.c */
extern int odyssey2_framestart;
extern int odyssey2_videobank;

extern extern int odyssey2_load_rom (int id);


/* vidhrdw/odyssey2.c */
extern int odyssey2_vh_hpos;

extern extern void odyssey2_vh_init_palette(UBytePtr game_palette, unsigned short *game_colortable,const UBytePtr color_prom);
extern extern void odyssey2_vh_write(int data);
extern void odyssey2_vh_update(int data);
extern 




/* i/o ports */
extern READ_HANDLER ( odyssey2_MAINRAM_r );
extern WRITE_HANDLER ( odyssey2_MAINRAM_w );

extern 
extern WRITE_HANDLER ( odyssey2_putp1 );
