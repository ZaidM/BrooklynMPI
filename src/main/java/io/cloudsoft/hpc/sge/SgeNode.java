package io.cloudsoft.hpc.sge;


import brooklyn.config.ConfigKey;
import brooklyn.entity.annotation.Effector;
import brooklyn.entity.annotation.EffectorParam;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.MethodEffector;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.java.UsesJmx;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.Sensors;
import brooklyn.util.flags.SetFromFlag;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import java.util.List;
import java.util.Map;

@ImplementedBy(SgeNodeImpl.class)
public interface SgeNode extends SoftwareProcess, UsesJmx {


    ConfigKey<String> SGE_CLUSTER_NAME = ConfigKeys.newStringConfigKey("sge.cluster.name", "name of the sge cluster", "brooklyn_sge_cluster");
    BasicAttributeSensorAndConfigKey<Boolean> MASTER_FLAG = new BasicAttributeSensorAndConfigKey<Boolean>(Boolean.class, "sge.masterFlag", "indicates whether this node is the master", Boolean.FALSE);
    ConfigKey<SgeNode> SGE_MASTER = ConfigKeys.newConfigKey(SgeNode.class, "sge.master.node");
    ConfigKey<String> SGE_ROOT = ConfigKeys.newStringConfigKey("sge.root", "name of the sge root folder", "/opt/sge6/");
    ConfigKey<String> SGE_ADMIN = ConfigKeys.newStringConfigKey("sge.admin", "name of the sge admin user", "sgeadmin");
    ConfigKey<String> SGE_PE_NAME = ConfigKeys.newStringConfigKey("sge.pe.name", "name of the parallel environment for SGE");
    AttributeSensor<String> MASTER_PUBLIC_SSH_KEY = Sensors.newStringSensor("sge.master.publicSshKey");
    AttributeSensor<String> SGE_ARCH = Sensors.newStringSensor("sge.arch", "the type of architecture for the installed sge node");
    AttributeSensor<List<String>> SGE_HOSTS = Sensors.newSensor(new TypeToken<List<String>>() {
    }, "sge.hosts", "Hostnames of all active sge nodes in the cluster (public hostname/IP)");

    //sensors to be fetched using qstat -f -xml

    AttributeSensor<String> SGE_QUEUE_QTYPE = Sensors.newStringSensor("sge.queue.qtype");
    AttributeSensor<String> SGE_QUEUE_SLOTS_USED = Sensors.newStringSensor("sge.queue.slots_used");
    AttributeSensor<String> SGE_QUEUE_SLOTS_RESERVED = Sensors.newStringSensor("sge.queue.slots.reserved");
    AttributeSensor<String> SGE_QUEUE_SLOTS_TOTAL = Sensors.newStringSensor("sge.queue.slots.total");
    AttributeSensor<String> SGE_QUEUE_LOAD_AVG = Sensors.newStringSensor("sge.queue.load.avg");
    AttributeSensor<String> SGE_QUEUE_NAME = Sensors.newStringSensor("sge.queue.name");
    AttributeSensor<String> SGE_QUEUE_ARCH = Sensors.newStringSensor("sge.queue.arch");



    AttributeSensor<String> SGE_QHOST_NUM_PROC = Sensors.newStringSensor("sge.qhost.num.proc");
    AttributeSensor<String> SGE_QHOST_LOAD_AVG = Sensors.newStringSensor("sge.qhost.load.avg");
    AttributeSensor<String> SGE_QHOST_MEM_TOTAL = Sensors.newStringSensor("sge.qhost.mem.total");
    AttributeSensor<String> SGE_QHOST_MEM_USED = Sensors.newStringSensor("sge.qhost.mem.used");
    AttributeSensor<String> SGE_QHOST_SWAP_TOTAL = Sensors.newStringSensor("sge.qhost.swap.total");
    AttributeSensor<String> SGE_QHOST_SWAP_USED = Sensors.newStringSensor("sge.qhost.swap.used");

    ConfigKey<String> MPI_VERSION = ConfigKeys.newStringConfigKey("mpi.version", "version of MPI", "1.6.5");

    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = new BasicAttributeSensorAndConfigKey<String>(
            SoftwareProcess.DOWNLOAD_URL, "thing");


    @SetFromFlag("downloadAddonUrls")
    BasicAttributeSensorAndConfigKey<Map<String, String>> DOWNLOAD_ADDON_URLS = new BasicAttributeSensorAndConfigKey<Map<String, String>>(
            SoftwareProcess.DOWNLOAD_ADDON_URLS, ImmutableMap.of(
            "mpi", "http://developers.cloudsoftcorp.com/brooklyn/repository/io-mpi/${addonversion}/openmpi-${addonversion}-withsge.tar.gz"));


    public static final MethodEffector<Void> UPDATE_HOSTS = new MethodEffector<Void>(SgeNode.class, "updateHosts");
    public static final MethodEffector<Void> UPDATE_PE = new MethodEffector<Void>(SgeNode.class, "updatePE");
    public static final MethodEffector<Void> ADD_SLAVE = new MethodEffector<Void>(SgeNode.class, "addSlave");
    public static final MethodEffector<Void> REMOVE_SLAVE = new MethodEffector<Void>(SgeNode.class, "removeSlave");
    AttributeSensor<Integer> NUM_OF_PROCESSORS = Sensors.newIntegerSensor("sge.num_of_processors", "attribute that shows the number of proceesors");

    @SetFromFlag("SGEConfigTemplate")
    ConfigKey<String> SGE_CONFIG_TEMPLATE_URL = ConfigKeys.newStringConfigKey(
            "sge.config.template", "Template file (in freemarker format) for configuring the io installation",
            "classpath://sge_installation");

    @SetFromFlag("SGEProfileTemplate")
    ConfigKey<String> SGE_PROFILE_TEMPLATE_URL = ConfigKeys.newStringConfigKey(
            "sge.config.profile", "Template file (in freemarker format) for setting the environment variables for sge",
            "classpath://sge_profile");

    @SetFromFlag("SGEParallelEnvironment")
    ConfigKey<String> SGE_PE_TEMPLATE_URL = ConfigKeys.newStringConfigKey(
            "sge.pe.template.url", "Template file (in freemarker format) for setting the environment variables for sge parallel environment",
            "classpath://sge_pe_template");


    @Effector(description = "updates the hosts list for the node")
    public void updateHosts(@EffectorParam(name = "mpiHosts", description = "list of all mpi hosts in the cluster") List<String> mpiHosts);

    public Boolean isMaster();

    @Effector(description = "Run on the master, will remove the slave-node's jobs")
    public void removeSlave(@EffectorParam(name = "slave") SgeNode slave);

    @Effector(description = "Run on the master, will add the slave-node so jobs can be executed on it")
    public void addSlave(@EffectorParam(name = "slave") SgeNode slave);

    public String getClusterName();

    @Effector(description = "Updates the Parallel environment and adds a new one if it doesn't exists")
    public void updatePE(@EffectorParam(name = "peName") String peName, @EffectorParam(name = "numOfProcessors") Integer numOfProcessors);

    public String getPEname();

    public String getSgeRoot();
}
