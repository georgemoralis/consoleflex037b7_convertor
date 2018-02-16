
int lisa_floppy_init(int id);
void lisa_floppy_exit(int id);



READ_HANDLER ( lisa_fdc_io_r );
WRITE_HANDLER ( lisa_fdc_io_w );
READ_HANDLER ( lisa_fdc_r );
WRITE_HANDLER ( lisa_fdc_w );
READ_HANDLER ( lisa_r );
WRITE_HANDLER ( lisa_w );


