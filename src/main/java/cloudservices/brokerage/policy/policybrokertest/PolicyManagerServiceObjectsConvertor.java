/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.policy.policybrokertest;

import cloudservices.brokerage.policy.serviceexecutor.service.Proposition;
import cloudservices.brokerage.policy.serviceexecutor.service.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import cloudservices.brokerage.policy.serviceexecutor.service.State;
import cloudservices.brokerage.policy.serviceexecutor.service.State.Params;
import cloudservices.brokerage.policy.serviceexecutor.service.State.Params.Entry;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/
 */
public class PolicyManagerServiceObjectsConvertor {

    public static Proposition convert(cloudservices.brokerage.policy.policycommons.model.entities.Proposition proposition) {
        Proposition ret = new Proposition();
        ret.setId(proposition.getId());
        ret.setName(proposition.getName());
        ret.setValid(proposition.isValid());
        return ret;
    }

    public static Service convert(cloudservices.brokerage.policy.policycommons.model.entities.Service service) {
        Service ret = new Service();
        ret.setId(service.getId());
        ret.setName(service.getName());
        ret.setServicesStr(service.getServicesStr());
        ret.setWSDLURL(service.getWSDLURL());
        return ret;
    }

    public static List<Proposition> propositionListConvert(Set<cloudservices.brokerage.policy.policycommons.model.entities.Proposition> propositions) {
        if (propositions == null) {
            return null;
        }
        List<Proposition> ret = new ArrayList<>();
        for (cloudservices.brokerage.policy.policycommons.model.entities.Proposition proposition : propositions) {
            ret.add(convert(proposition));
        }
        return ret;
    }

    public static State convert(cloudservices.brokerage.policy.policycommons.model.entities.State currentState) {
        State state = new State();
        state.setNumber(currentState.getNumber());
        state.getPropositions().addAll(propositionListConvert(currentState.getPropositions()));
        state.setParams(convertParams(currentState.getParams()));
        return state;
    }

    public static Params convertParams(HashMap<String, Object> params) {
        Params ret = new Params();
        for (Map.Entry<String, Object> en : params.entrySet()) {
            Entry entry = new Entry();
            entry.setKey(en.getKey());
            List<String> seeds = (List<String>) en.getValue();
            entry.setValue(seeds.get(0));
            ret.getEntry().add(entry);
        }
        return ret;
    }
}
