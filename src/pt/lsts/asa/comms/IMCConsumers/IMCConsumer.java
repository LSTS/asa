package pt.lsts.asa.comms.IMCConsumers;

import pt.lsts.imc.IMCMessage;

/**
 * Created by jloureiro on 2/25/15.
 */
public interface IMCConsumer {
    public void consume(IMCMessage msg);
}
