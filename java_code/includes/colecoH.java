/* machine/coleco.c */
extern UBytePtr coleco_ram;
extern UBytePtr coleco_cartridge_rom;

extern int coleco_id_rom (int id);
extern int coleco_load_rom (int id);
extern READ_HANDLER  ( coleco_ram_r );
extern WRITE_HANDLER ( coleco_ram_w );
extern READ_HANDLER  ( coleco_paddle_r );
extern WRITE_HANDLER ( coleco_paddle_toggle_1_w );
extern WRITE_HANDLER ( coleco_paddle_toggle_2_w );
extern READ_HANDLER  ( coleco_VDP_r );
extern WRITE_HANDLER ( coleco_VDP_w );


/* vidhrdw/coleco.c */
extern extern extern 


