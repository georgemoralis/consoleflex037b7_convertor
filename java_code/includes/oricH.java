/* from machine/oric.c */

int oric_load_rom (int id);
READ_HANDLER ( oric_IO_r );
WRITE_HANDLER ( oric_IO_w );

/* from vidhrdw/oric.c */

extern UBytePtr oric_IO;

// int oric_ram_r (int offset);
// void oric_ram_w (int offset, int data);



/* from vidhrdw */
void oric_set_powerscreen_mode (int mode);
void oric_set_flash_show (int mode);

int oric_extract_file_from_tape (int filenum);





