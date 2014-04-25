package cloudservices.brokerage.policy.policybrokertest;

import cloudservices.brokerage.commons.utils.file_utils.ResourceFileUtil;
import cloudservices.brokerage.commons.utils.logging.LoggerSetup;
import cloudservices.brokerage.policy.policycommons.model.DAO.BaseDAO;
import cloudservices.brokerage.policy.policycommons.model.DAO.DAOException;
import cloudservices.brokerage.policy.policycommons.model.DAO.PropositionDAO;
import cloudservices.brokerage.policy.policycommons.model.DAO.ServiceDAO;
import cloudservices.brokerage.policy.policycommons.model.entities.Proposition;
import cloudservices.brokerage.policy.policycommons.model.entities.Service;
import cloudservices.brokerage.policy.policycommons.model.entities.State;
import cloudservices.brokerage.policy.serviceexecutor.service.DAOException_Exception;
import cloudservices.brokerage.policy.serviceexecutor.service.IOException_Exception;
import cloudservices.brokerage.policy.serviceexecutor.service.ServiceExecutionException_Exception;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.cfg.Configuration;

/**
 * Hello world!
 *
 */
public class App {

    private final static Logger LOGGER = Logger.getLogger(App.class
            .getName());

    private void setupLoggers() throws IOException {
        LoggerSetup.setup(ResourceFileUtil.getResourcePath("log.txt"), ResourceFileUtil.getResourcePath("log.html"));
        LoggerSetup.log4jSetup(ResourceFileUtil.getResourcePath("log4j.properties"),
                ResourceFileUtil.getResourcePath("hibernate.log"));
    }

    public static void main(String[] args) {
        try {
            App test = new App();
            test.setupLoggers();
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            BaseDAO.openSession(configuration);

            //****************SAMPLE DATA******************
            ServiceDAO serviceDAO = new ServiceDAO();
            Service service = serviceDAO.getByName("Composite Level 3 Crawler");
            PropositionDAO pDAO = new PropositionDAO();
            Set<Proposition> initials = new HashSet<>();
            initials.add(pDAO.getByName("Seeds Available")); //Seeds Available
            HashMap<String, Object> params = new HashMap<>();
            List<String> seeds = new ArrayList<>();
            seeds.add("http://www.arashkhodadadi.com/");
            params.put("seeds", seeds);
            State initialState = new State();
            initialState.setNumber(0);
            initialState.setPropositions(initials);
            initialState.setParams(params);
            State goalState = new State();
            Set<Proposition> goals = new HashSet<>();
            goals.add(pDAO.getByName("Level 3 Completed"));
            goalState.setPropositions(goals);

            long startTime = System.currentTimeMillis();
            LOGGER.log(Level.INFO, "Broker Test Start");
            LOGGER.log(Level.INFO, "Seeds= {0}", seeds);

            List<Object> result = test.executeService(service, initialState, goalState);
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            LOGGER.log(Level.INFO, "Broker Test End in {0}ms", totalTime);
            LOGGER.log(Level.INFO, "Result {0} urls: {1}", new Object[]{result.size(), result.toString()});
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            BaseDAO.closeSession();
        }
    }

    private List<Object> executeService(Service call, State initial, State goal) throws DAOException_Exception, IOException_Exception, ServiceExecutionException_Exception {
        cloudservices.brokerage.policy.serviceexecutor.service.ServiceExecutorWS_Service service = new cloudservices.brokerage.policy.serviceexecutor.service.ServiceExecutorWS_Service();
        cloudservices.brokerage.policy.serviceexecutor.service.ServiceExecutorWS port = service.getServiceExecutorWSPort();
        cloudservices.brokerage.policy.serviceexecutor.service.Service toCall = PolicyManagerServiceObjectsConvertor.convert(call);
        cloudservices.brokerage.policy.serviceexecutor.service.State initialState = PolicyManagerServiceObjectsConvertor.convert(initial);
        cloudservices.brokerage.policy.serviceexecutor.service.State goalState = PolicyManagerServiceObjectsConvertor.convert(goal);
        return port.executeService(toCall, initialState, goalState);
    }
}
