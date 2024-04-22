package hirs.utils.tpm.eventlog.events;

import hirs.utils.HexUtils;
import hirs.utils.tpm.eventlog.spdm.SpdmHa;
import hirs.utils.tpm.eventlog.spdm.SpdmMeasurementBlock;
import hirs.utils.tpm.eventlog.uefi.UefiConstants;
import lombok.Getter;

import java.io.UnsupportedEncodingException;


/**
 * Class to process the DEVICE_SECURITY_EVENT_DATA_HEADER.
 * DEVICE_SECURITY_EVENT_DATA_HEADER contains the measurement(s) and hash algorithm identifier
 * returned by the SPDM "GET_MEASUREMENTS" function.
 *
 * HEADERS defined by PFP v1.06 Rev 52:
 * <p>
 * typedef struct tdDEVICE_SECURITY_EVENT_DATA_HEADER {
 *      UINT8                           Signature[16];
 *      UINT16                          Version;
 *      UINT16                          Length;
 *      UINT32                          SpdmHashAlg;
 *      UINT32                          DeviceType;
 *      SPDM_MEASUREMENT_BLOCK          SpdmMeasurementBlock;
 *      UINT64                          DevicePathLength;
 *      UNIT8                           DevicePath[DevicePathLength]
 * } DEVICE_SECURITY_EVENT_DATA_HEADER;
 * <p>
 * Assumption: there is only 1 SpdmMeasurementBlock per event. Need more test patterns to verify.
 */
public class DeviceSecurityEventDataHeader extends DeviceSecurityEventDataHeaderBase {

    /**
     * Event data length.
     */
    @Getter
    private int length = 0;
    /**
     * SPDM hash algorithm.
     */
    @Getter
    private int spdmHashAlgo = -1;
    /**
     * SPDM Measurement Block list.   -implement this if there can be multiple SPDM blocks in one event
     */
    //private List<SpdmMeasurementBlock> spdmMeasurementBlockList;
    /**
     * SPDM Measurement Block.
     */
    private SpdmMeasurementBlock spdmMeasurementBlock = null;

    /**
     * DeviceSecurityEventDataHeader Constructor.
     *
     * @param dSEDbytes byte array holding the DeviceSecurityEventData.
     */
    public DeviceSecurityEventDataHeader(final byte[] dSEDbytes) throws UnsupportedEncodingException {

        super(dSEDbytes);

        byte[] lengthBytes = new byte[UefiConstants.SIZE_2];
        System.arraycopy(dSEDbytes, 18, lengthBytes, 0,
                UefiConstants.SIZE_2);
        length = HexUtils.leReverseInt(lengthBytes);

        byte[] spdmHashAlgoBytes = new byte[UefiConstants.SIZE_4];
        System.arraycopy(dSEDbytes, UefiConstants.OFFSET_20, spdmHashAlgoBytes, 0,
                UefiConstants.SIZE_4);
        spdmHashAlgo = HexUtils.leReverseInt(spdmHashAlgoBytes);

        extractDeviceType(dSEDbytes, 24);

        // get the size of the SPDM Measurement Block
        byte[] sizeOfSpdmMeasBlockBytes = new byte[UefiConstants.SIZE_2];
        System.arraycopy(dSEDbytes, 30, sizeOfSpdmMeasBlockBytes, 0,
                UefiConstants.SIZE_2);
        int sizeOfSpdmMeas = HexUtils.leReverseInt(sizeOfSpdmMeasBlockBytes);
        int sizeOfSpdmMeasBlock = sizeOfSpdmMeas + 4;   // header is 4 bytes

        // extract the bytes from the SPDM Measurement Block
        byte[] spdmMeasBlockBytes = new byte[sizeOfSpdmMeasBlock];
        System.arraycopy(dSEDbytes, 28, spdmMeasBlockBytes, 0,
                sizeOfSpdmMeasBlock);
        spdmMeasurementBlock = new SpdmMeasurementBlock(spdmMeasBlockBytes);


        // (can there be many >1 spdm block per event ?)

//        byte[] algorithmIDBytes = new byte[UefiConstants.SIZE_2];
//        int algLocation = UefiConstants.SIZE_28;
//        for (int i = 0; i < numberOfAlg; i++) {
//            System.arraycopy(efiSpecId, algLocation + UefiConstants.OFFSET_4 * i, algorithmIDBytes,
//                    0, UefiConstants.SIZE_2);
//            String alg = TcgTpmtHa.tcgAlgIdToString(HexUtils.leReverseInt(algorithmIDBytes));
//            algList.add(alg);
//        }

        int devPathLenStartByte = 28 + sizeOfSpdmMeasBlock;
        extractDevicePathAndFinalSize(dSEDbytes, devPathLenStartByte);

    }


    /**
     * Returns a human readable description of the data within this structure.
     *
     * @return a description of this structure.
     */
    public String toString() {
        String dsedHeaderInfo = "";

        dsedHeaderInfo += headerBaseToString();
        String spdmHashAlgoStr = SpdmHa.tcgAlgIdToString(spdmHashAlgo);
        dsedHeaderInfo += "\n   SPDM Hash Algorithm = " + spdmHashAlgoStr;
        dsedHeaderInfo += "\n   SPDM Measurement Block:";
        dsedHeaderInfo += spdmMeasurementBlock.toString();

        return dsedHeaderInfo;
    }
}
