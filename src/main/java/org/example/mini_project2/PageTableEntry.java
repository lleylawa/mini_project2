package org.example.mini_project2;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PageTableEntry {
    private final SimpleIntegerProperty pageNumber;
    private final SimpleIntegerProperty frameNumber;
    private final SimpleStringProperty validBit;
    private final SimpleStringProperty dirtyBit;
    private final SimpleStringProperty referenceBit;

    public PageTableEntry(int pageNum) {
        this.pageNumber = new SimpleIntegerProperty(pageNum);
        this.frameNumber = new SimpleIntegerProperty(-1); // -1: Not Loaded
        this.validBit = new SimpleStringProperty("I"); // I: Invalid
        this.dirtyBit = new SimpleStringProperty("N"); // N: Not Dirty
        this.referenceBit = new SimpleStringProperty("0");
    }

    // --- Обновление полей (используется логикой) ---
    public void setLoaded(int frame) {
        this.frameNumber.set(frame);
        this.validBit.set("V");
        this.referenceBit.set("1");
    }

    public void setEvicted() {
        this.frameNumber.set(-1);
        this.validBit.set("I");
        this.dirtyBit.set("N");
        this.referenceBit.set("0");
    }

    public void access(boolean isWriteOperation) {
        if (validBit.get().equals("V")) {
            this.referenceBit.set("1");
            if (isWriteOperation) {
                this.dirtyBit.set("D"); // D: Dirty
            }
        }
    }

    // --- Геттеры для JavaFX TableView ---
    public int getPageNumber() { return pageNumber.get(); }
    public int getFrameNumber() { return frameNumber.get(); }
    public String getValidBit() { return validBit.get(); }
    public String getDirtyBit() { return dirtyBit.get(); }
    public String getReferenceBit() { return referenceBit.get(); }

    // --- Геттеры для доступа к свойствам ---
    public SimpleIntegerProperty frameNumberProperty() { return frameNumber; }
    public SimpleStringProperty validBitProperty() { return validBit; }
    public SimpleStringProperty dirtyBitProperty() { return dirtyBit; }
    public SimpleStringProperty referenceBitProperty() { return referenceBit; }
}