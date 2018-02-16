/* from machine\pce.c */

extern UBytePtr pce_user_ram; /* scratch RAM at F8 */
extern UBytePtr pce_save_ram; /* battery backed RAM at F7 */
extern int pce_load_rom(int id);
extern int pce_id_rom (int id);
extern extern extern WRITE_HANDLER ( pce_joystick_w );
extern READ_HANDLER ( pce_joystick_r );


