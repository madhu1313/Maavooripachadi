package com.maavooripachadi.shipping;
import org.springframework.stereotype.Service;
@Service public class PincodeService { public boolean serviceable(String pincode){ return pincode!=null && pincode.matches("\\d{6}"); } }
