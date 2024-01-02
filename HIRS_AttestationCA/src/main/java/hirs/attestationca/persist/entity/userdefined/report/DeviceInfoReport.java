package hirs.attestationca.persist.entity.userdefined.report;

import hirs.attestationca.persist.entity.AbstractEntity;
import hirs.attestationca.persist.entity.userdefined.info.FirmwareInfo;
import hirs.attestationca.persist.entity.userdefined.info.HardwareInfo;
import hirs.attestationca.persist.entity.userdefined.info.NetworkInfo;
import hirs.attestationca.persist.entity.userdefined.info.OSInfo;
import hirs.attestationca.persist.entity.userdefined.info.TPMInfo;
import hirs.utils.VersionHelper;
import hirs.utils.enums.DeviceInfoEnums;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

/**
 * A <code>DeviceInfoReport</code> is a <code>Report</code> used to transfer the
 * information about the device. This <code>Report</code> includes the network,
 * OS, and TPM information.
 */
@Log4j2
@Getter
@NoArgsConstructor
@Entity
public class DeviceInfoReport extends AbstractEntity implements Serializable {

    @XmlElement
    @Embedded
    private NetworkInfo networkInfo;

    @XmlElement
    @Embedded
    private OSInfo osInfo;

    @XmlElement
    @Embedded
    private FirmwareInfo firmwareInfo;

    @XmlElement
    @Embedded
    private HardwareInfo hardwareInfo;

    @XmlElement
    @Embedded
    private TPMInfo tpmInfo;

    @XmlElement
    @Column(nullable = false)
    private String clientApplicationVersion;

    @Setter
    @XmlElement
    @Transient
    private String paccorOutputString;

    /**
     * Constructor used to create a <code>DeviceInfoReport</code>. The
     * information cannot be changed after the <code>DeviceInfoReport</code> is
     * created.
     *
     * @param networkInfo
     *            NetworkInfo object, cannot be null
     * @param osInfo
     *            OSInfo object, cannot be null
     * @param firmwareInfo
     *            FirmwareInfo object, cannot be null
     * @param hardwareInfo
     *            HardwareInfo object, cannot be null
     * @param tpmInfo
     *            TPMInfo object, may be null if a TPM is not available on the
     *            device
     */
    public DeviceInfoReport(final NetworkInfo networkInfo, final OSInfo osInfo,
                            final FirmwareInfo firmwareInfo, final HardwareInfo hardwareInfo,
                            final TPMInfo tpmInfo) {
        this(networkInfo, osInfo, firmwareInfo, hardwareInfo, tpmInfo, VersionHelper.getVersion());
    }

    /**
     * Constructor used to create a <code>DeviceInfoReport</code>. The
     * information cannot be changed after the <code>DeviceInfoReport</code> is
     * created.
     *
     * @param networkInfo
     *            NetworkInfo object, cannot be null
     * @param osInfo
     *            OSInfo object, cannot be null
     * @param firmwareInfo
     *            FirmwareInfo object, cannot be null
     * @param hardwareInfo
     *            HardwareInfo object, cannot be null
     * @param tpmInfo
     *            TPMInfo object, may be null if a TPM is not available on the
     *            device
     * @param clientApplicationVersion
     *            string representing the version of the client that submitted this report,
     *            cannot be null
     */
    public DeviceInfoReport(final NetworkInfo networkInfo, final OSInfo osInfo,
                            final FirmwareInfo firmwareInfo, final HardwareInfo hardwareInfo,
                            final TPMInfo tpmInfo, final String clientApplicationVersion) {
        setNetworkInfo(networkInfo);
        setOSInfo(osInfo);
        setFirmwareInfo(firmwareInfo);
        setHardwareInfo(hardwareInfo);
        setTPMInfo(tpmInfo);
        this.clientApplicationVersion = clientApplicationVersion;
    }

    /**
     * Retrieves the NetworkInfo for this <code>DeviceInfoReport</code>.
     *
     * @return networkInfo
     */
    public NetworkInfo getNetworkInfo() {
        /*
         * Hibernate bug requires this
         * https://hibernate.atlassian.net/browse/HHH-7610
         * without null may be returned, which this interface does not support
         */
        if (networkInfo == null) {
            networkInfo = new NetworkInfo(null, null, null);
        }
        return new NetworkInfo(networkInfo.getHostname(),
                networkInfo.getIpAddress(), networkInfo.getMacAddress());
    }

    /**
     * Retrieves the OSInfo for this <code>DeviceInfoReport</code>.
     *
     * @return osInfo
     */
    public OSInfo getOSInfo() {
        /*
         * Hibernate bug requires this
         * https://hibernate.atlassian.net/browse/HHH-7610
         * without null may be returned, which this interface does not support
         */
        if (osInfo == null) {
            osInfo = new OSInfo(DeviceInfoEnums.NOT_SPECIFIED, DeviceInfoEnums.NOT_SPECIFIED,
                    DeviceInfoEnums.NOT_SPECIFIED, DeviceInfoEnums.NOT_SPECIFIED,
                    DeviceInfoEnums.NOT_SPECIFIED);
        }
        return osInfo;
    }

    /**
     * Retrieves the FirmwareInfo for this <code>DeviceInfoReport</code>.
     *
     * @return osInfo
     */
    public FirmwareInfo getFirmwareInfo() {
        /*
         * Hibernate bug requires this
         * https://hibernate.atlassian.net/browse/HHH-7610
         * without null may be returned, which this interface does not support
         */
        if (firmwareInfo == null) {
            firmwareInfo = new FirmwareInfo(DeviceInfoEnums.NOT_SPECIFIED,
                    DeviceInfoEnums.NOT_SPECIFIED, DeviceInfoEnums.NOT_SPECIFIED);
        }
        return firmwareInfo;
    }

    /**
     * Retrieves the OSInfo for this <code>DeviceInfoReport</code>.
     *
     * @return osInfo
     */
    public HardwareInfo getHardwareInfo() {
        /*
         * Hibernate bug requires this
         * https://hibernate.atlassian.net/browse/HHH-7610
         * without null may be returned, which this interface does not support
         */
        if (hardwareInfo == null) {
            hardwareInfo = new HardwareInfo(
                    DeviceInfoEnums.NOT_SPECIFIED,
                    DeviceInfoEnums.NOT_SPECIFIED,
                    DeviceInfoEnums.NOT_SPECIFIED,
                    DeviceInfoEnums.NOT_SPECIFIED,
                    DeviceInfoEnums.NOT_SPECIFIED,
                    DeviceInfoEnums.NOT_SPECIFIED
            );
        }
        return hardwareInfo;
    }

    private void setNetworkInfo(NetworkInfo networkInfo) {
        if (networkInfo == null) {
            log.error("NetworkInfo cannot be null");
            throw new NullPointerException("network info");
        }
        this.networkInfo = networkInfo;
    }

    private void setOSInfo(OSInfo osInfo) {
        if (osInfo == null) {
            log.error("OSInfo cannot be null");
            throw new NullPointerException("os info");
        }
        this.osInfo = osInfo;
    }

    private void setFirmwareInfo(FirmwareInfo firmwareInfo) {
        if (firmwareInfo == null) {
            log.error("FirmwareInfo cannot be null");
            throw new NullPointerException("firmware info");
        }
        this.firmwareInfo = firmwareInfo;
    }

    private void setHardwareInfo(HardwareInfo hardwareInfo) {
        if (hardwareInfo == null) {
            log.error("HardwareInfo cannot be null");
            throw new NullPointerException("hardware info");
        }
        this.hardwareInfo = hardwareInfo;
    }

    private void setTPMInfo(TPMInfo tpmInfo) {
        this.tpmInfo = tpmInfo;
    }
}
