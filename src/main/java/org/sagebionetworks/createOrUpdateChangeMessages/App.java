package org.sagebionetworks.createOrUpdateChangeMessages;

import java.io.IOException;

import org.sagebionetworks.client.SynapseAdminClient;
import org.sagebionetworks.client.SynapseAdminClientImpl;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.TeamMember;

import com.csvreader.CsvReader;

public class App {

    private static SynapseAdminClient adminSynapse;
    private final static String LOCAL_AUTH = "http://localhost:8080/services-repository-develop-SNAPSHOT/auth/v1";
    private final static String LOCAL_REPO = "http://localhost:8080/services-repository-develop-SNAPSHOT/repo/v1";
    private final static String LOCAL_FILE = "http://localhost:8080/services-repository-develop-SNAPSHOT/file/v1";
    private final static String STAGING_AUTH = "https://auth-staging.prod.sagebase.org/auth/v1";
    private final static String STAGING_REPO = "https://repo-staging.prod.sagebase.org/repo/v1";
    private final static String STAGING_FILE = "https://file-staging.prod.sagebase.org/file/v1";

    public static void main(String[] args) {
        if (args.length != 5) printUsage();
        String stack = args[0];
        String username = args[1];
        String apiKey = args[2];
        String filePath = args[3];
        String type = args[4];

        adminSynapse = new SynapseAdminClientImpl();
        if (stack != null && !stack.equals("prod")) {
            setEndPoint(adminSynapse, stack);
        }
        adminSynapse.setUserName(username);
        adminSynapse.setApiKey(apiKey);

        process(adminSynapse, filePath, type);
    }

    private static void process(SynapseAdminClient adminSynapse, String filePath, String type) {
        if (type.equals("entity")) {
            try {
                processEntity(adminSynapse, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("team")) {
            try {
                processTeam(adminSynapse, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("eval")) {
            try {
                processEval(adminSynapse, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            printUsage();
        }
    }

    private static void processEval(SynapseAdminClient adminSynapse, String filePath) throws IOException {
        CsvReader reader = new CsvReader(filePath);
        while (reader.readRecord()) {
            String id = reader.get(0);
            AccessControlList acl;
            try {
                System.out.println("Getting ACL with Evaluation Id = " + id);
                acl = adminSynapse.getEvaluationAcl(id);
                System.out.println("Updating ACL: " + acl.toString());
                adminSynapse.updateEvaluationAcl(acl);
            } catch (SynapseException e) {
                e.printStackTrace();
            }
        }
        reader.close();
    }

    private static void processTeam(SynapseAdminClient adminSynapse, String filePath) throws IOException {
        CsvReader reader = new CsvReader(filePath);
        while (reader.readRecord()) {
            String teamId = reader.get(0);
            String memberId = reader.get(1);
            try {
                System.out.println("Getting ACL with Team Id = " + teamId + " member Id = " + memberId);
                TeamMember member = adminSynapse.getTeamMember(teamId, memberId);
                System.out.println("Updating status: " + member.getIsAdmin());
                adminSynapse.setTeamMemberPermissions(teamId, memberId, member.getIsAdmin());
            } catch (SynapseException e) {
                e.printStackTrace();
            }
        }
        reader.close();
    }

    private static void processEntity(SynapseAdminClient adminSynapse, String filePath) throws IOException {
        CsvReader reader = new CsvReader(filePath);
        while (reader.readRecord()) {
            String id = reader.get(0);
            AccessControlList acl;
            try {
                System.out.println("Getting ACL with entityId = " + id);
                acl = adminSynapse.getACL(id.toString());
                System.out.println("Updating ACL: " + acl.toString());
                adminSynapse.updateACL(acl);
            } catch (SynapseException e) {
                e.printStackTrace();
            }
        }
        reader.close();
    }

    private static void printUsage() {
        System.out.println("Usage: ");
        System.out.println("gradle run -Pstack=<prod/local/staging> -Pusername=<synapseUsername> -PapiKey=<apiKey> -PfilePath=<filePath>");
        System.exit(0);
    }

    private static void setEndPoint(SynapseAdminClient adminSynapse, String stack) {
        if (stack == null || (!stack.equals("staging") && (!stack.equals("local")))) printUsage();

        if (stack.equals("staging")) {
            adminSynapse.setAuthEndpoint(STAGING_AUTH);
            adminSynapse.setRepositoryEndpoint(STAGING_REPO);
            adminSynapse.setFileEndpoint(STAGING_FILE);
        } else {
            adminSynapse.setAuthEndpoint(LOCAL_AUTH);
            adminSynapse.setRepositoryEndpoint(LOCAL_REPO);
            adminSynapse.setFileEndpoint(LOCAL_FILE);
        }
    }
}
