/* machine/advision.c */
extern int advision_framestart;
/*extern int advision_videoenable;*/
extern int advision_videobank;

extern extern int advision_id_rom (int id);
extern int advision_load_rom (int id);
extern READ_HANDLER ( advision_MAINRAM_r);
extern extern extern extern READ_HANDLER ( advision_getp1 );
extern READ_HANDLER ( advision_getp2 );
extern READ_HANDLER ( advision_gett0 );
extern READ_HANDLER ( advision_gett1 );


/* vidhrdw/advision.c */
extern int advision_vh_hpos;

extern extern void advision_vh_init_palette(UBytePtr game_palette, unsigned short *game_colortable,const UBytePtr color_prom);
extern extern void advision_vh_write(int data);
extern void advision_vh_update(int data);
extern 
