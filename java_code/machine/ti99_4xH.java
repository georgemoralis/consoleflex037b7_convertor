/*
  header file for /machine/ti99_4x.c
*/

/* variables */

extern UBytePtr ti99_scratch_RAM;
extern UBytePtr ti99_xRAM_low;
extern UBytePtr ti99_xRAM_high;

extern UBytePtr ti99_cart_mem;
extern UBytePtr ti99_DSR_mem;


/* protos for support code */


int ti99_floppy_init(int id);

int ti99_cassette_init(int id);
void ti99_cassette_exit(int id);

int ti99_load_rom(int id);
void ti99_rom_cleanup(int id);
int ti99_id_rom(int id);


READ_HANDLER ( ti99_rw_null8bits );
WRITE_HANDLER ( ti99_ww_null8bits );

READ_HANDLER ( ti99_rw_xramlow );
WRITE_HANDLER ( ti99_ww_xramlow );
READ_HANDLER ( ti99_rw_xramhigh );
WRITE_HANDLER ( ti99_ww_xramhigh );

READ_HANDLER ( ti99_rw_cartmem );
WRITE_HANDLER ( ti99_ww_cartmem );

READ_HANDLER ( ti99_rw_scratchpad );
WRITE_HANDLER ( ti99_ww_scratchpad );

READ_HANDLER ( ti99_rw_rvdp );
WRITE_HANDLER ( ti99_ww_wvdp );
READ_HANDLER ( ti99_rw_rspeech );
WRITE_HANDLER ( ti99_ww_wspeech );
READ_HANDLER ( ti99_rw_rgpl );

READ_HANDLER ( ti99_rw_disk );
WRITE_HANDLER ( ti99_ww_disk );
READ_HANDLER ( ti99_DSKget );
WRITE_HANDLER ( ti99_DSKROM );
WRITE_HANDLER ( ti99_DSKhold);
WRITE_HANDLER ( ti99_DSKheads );
WRITE_HANDLER ( ti99_DSKsel );
WRITE_HANDLER ( ti99_DSKside );



