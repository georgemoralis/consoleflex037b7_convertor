/******************************************************************************
    BBC Model B

    MESS Driver By:

	Gordon Jefferyes
	mess_bbc@gjeffery.dircon.co.uk

******************************************************************************/

extern 
extern extern extern 
int bbc_floppy_init(int);

void bbc_floppy_exit(int);

READ_HANDLER ( bbc_wd1770_read);
WRITE_HANDLER ( bbc_wd1770_write );



READ_HANDLER(bbc_i8271_read);
WRITE_HANDLER(bbc_i8271_write);

