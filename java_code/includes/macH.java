/* from machine/mac.c */
extern extern extern extern extern extern READ_HANDLER ( mac_via_r );
extern WRITE_HANDLER ( mac_via_w );
extern READ_HANDLER ( mac_autovector_r );
extern WRITE_HANDLER ( mac_autovector_w );
extern READ_HANDLER ( mac_iwm_r );
extern WRITE_HANDLER ( mac_iwm_w );
extern READ_HANDLER ( mac_scc_r );
extern WRITE_HANDLER ( mac_scc_w );
extern READ_HANDLER ( macplus_scsi_r );
extern WRITE_HANDLER ( macplus_scsi_w );
extern int mac_floppy_init(int id);
extern void mac_floppy_exit(int id);
extern extern void mac_scc_mouse_irq( int x, int y );

extern int mac_ram_size;
extern UINT8 *mac_ram_ptr;

/* from vidhrdw/mac.c */
extern extern extern 
/* from sndhrdw/mac.c */
extern int mac_sh_start( const struct MachineSound *msound );
extern extern 
extern void mac_enable_sound( int on );
extern void mac_set_buffer( int buffer );
extern void mac_set_volume( int volume );

void mac_sh_data_w(int index_loc);


