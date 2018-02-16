
void cbm_ieee_dav_w(int device, int data);
void cbm_ieee_nrfd_w(int device, int data);
void cbm_ieee_ndac_w(int device, int data);
void cbm_ieee_atn_w(int device, int data);
void cbm_ieee_eoi_w(int device, int data);
void cbm_ieee_data_w(int device, int data);


/* for debugging  */
READ_HANDLER(cbm_ieee_state);
