package org.sagebionetworks.aclscript;

import org.sagebionetworks.client.SynapseAdminClient;
import org.sagebionetworks.client.SynapseAdminClientImpl;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

    private static SynapseAdminClient adminSynapse;
/*    private final static String LOCAL_AUTH = "http://localhost:8080/services-repository-develop-SNAPSHOT/auth/v1";
    private final static String LOCAL_REPO = "http://localhost:8080/services-repository-develop-SNAPSHOT/repo/v1";
    private final static String LOCAL_FILE = "http://localhost:8080/services-repository-develop-SNAPSHOT/file/v1";
    private final static String STAGING_AUTH = "https://auth-staging.prod.sagebase.org/auth/v1";
    private final static String STAGING_REPO = "https://repo-staging.prod.sagebase.org/repo/v1";
    private final static String STAGING_FILE = "https://file-staging.prod.sagebase.org/file/v1";*/

    public static void main(String[] args) {
        if (args.length != 4) printUsage();
        String stack = args[0];
        String username = args[1];
        String password = args[2];
        String filePath = args[3];

        adminSynapse = new SynapseAdminClientImpl();
        if (stack != null && !stack.equals("prod")) {
            setEndPoint(adminSynapse, stack);
        }
        adminSynapse.setUserName(username);
        adminSynapse.setApiKey(password);
        System.out.println(adminSynapse.getAuthEndpoint());
        System.out.println(adminSynapse.getRepoEndpoint());
        System.out.println(adminSynapse.getFileEndpoint());

        process(adminSynapse, filePath);
    }

    private static void process(SynapseAdminClient adminSynapse, String filePath) {
        
    }

    private static void printUsage() {
        System.out.println("Usage: ");
        System.out.println("gradle run -Pstack=<prod/local/staging> -Pusername=<synapseUsername> -Ppassword=<password> -PfilePath=<filePath>");
        System.exit(0);
    }

    /*
     * Set the endpoints based on the ~/.aclscript/aclscript.config file
     */
    private static void setEndPoint(SynapseAdminClient adminSynapse, String stack) {
        if (stack == null || (!stack.equals("staging") && (!stack.equals("local")))) printUsage();
        final ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("/setting.xml");
        context.registerShutdownHook();
        AclscriptConfig config = context.getBean(AclscriptConfig.class);

        adminSynapse.setAuthEndpoint(config.getAuthEndpoint(stack));
        adminSynapse.setRepositoryEndpoint(config.getRepoEndpoint(stack));
        adminSynapse.setFileEndpoint(config.getFileEndpoint(stack));

/*        if (stack.equals("staging")) {
            adminSynapse.setAuthEndpoint(STAGING_AUTH);
            adminSynapse.setRepositoryEndpoint(STAGING_REPO);
            adminSynapse.setFileEndpoint(STAGING_FILE);
        } else {
            adminSynapse.setAuthEndpoint(LOCAL_AUTH);
            adminSynapse.setRepositoryEndpoint(LOCAL_REPO);
            adminSynapse.setFileEndpoint(LOCAL_FILE);
        }*/
        //context.close();
    }
}
