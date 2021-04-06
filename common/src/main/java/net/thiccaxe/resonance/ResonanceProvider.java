package net.thiccaxe.resonance;

public class ResonanceProvider {

    private Resonance instance;

    public ResonanceProvider(){}

    public void setInstance(Resonance instance) {
        this.instance = instance;
    }
    public Resonance getInstance() {
        return instance;
    }
}
