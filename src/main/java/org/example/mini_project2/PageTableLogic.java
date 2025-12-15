package org.example.mini_project2;

import java.util.*;

public class PageTableLogic {
    private final Map<Integer, PageTableEntry> pageTable;
    private final LinkedList<Integer> lruList; // MRU (front) -> LRU (back)
    private final int capacity;
    private int nextFreeFrame;

    public PageTableLogic(List<Integer> allPages, int capacity) {
        this.capacity = capacity;
        this.pageTable = new HashMap<>();
        this.lruList = new LinkedList<>();
        this.nextFreeFrame = 0;

        for (int page : allPages) {
            pageTable.put(page, new PageTableEntry(page));
        }
    }

    public Map<Integer, PageTableEntry> getPageTable() {
        return pageTable;
    }

    /**
     * Обработка одной ссылки на страницу.
     * @param page Номер страницы.
     * @param isWriteOperation True, если это операция записи (устанавливает Dirty Bit).
     * @return Сообщение о результате операции.
     */
    public String referencePage(int page, boolean isWriteOperation) {
        PageTableEntry entry = pageTable.get(page);
        if (entry == null) {
            return String.format("ERROR: Page %d is not part of the process address space.", page);
        }

        String operationType = isWriteOperation ? "WRITE" : "READ";
        String message;

        if (entry.getValidBit().equals("V")) {
            // --- PAGE HIT ---
            entry.access(isWriteOperation);
            lruList.remove((Integer) page);
            lruList.addFirst(page);
            message = String.format("HIT: Page %d accessed (Frame %d). Operation: %s", page, entry.getFrameNumber(), operationType);
        } else {
            // --- PAGE FAULT ---
            int frameToUse;
            String evictedInfo = "";

            if (lruList.size() < capacity) {
                // 1. Свободные кадры
                frameToUse = nextFreeFrame++;
            } else {
                // 2. LRU Replacement
                int evictedPage = lruList.removeLast();
                PageTableEntry evictedEntry = pageTable.get(evictedPage);
                frameToUse = evictedEntry.getFrameNumber();

                // Проверка Dirty Bit перед выселением
                if (evictedEntry.getDirtyBit().equals("D")) {
                    evictedInfo = String.format("EVICTED (Dirty) Page %d (Frame %d). Write-back required.", evictedPage, frameToUse);
                } else {
                    evictedInfo = String.format("EVICTED (Clean) Page %d (Frame %d).", evictedPage, frameToUse);
                }
                evictedEntry.setEvicted();
            }

            // Загрузка новой страницы
            lruList.addFirst(page);
            entry.setLoaded(frameToUse);
            if (isWriteOperation) {
                entry.access(true);
            }

            message = String.format("FAULT: %s LOADED Page %d into Frame %d. Operation: %s",
                    evictedInfo.isEmpty() ? "" : evictedInfo + " | ", page, frameToUse, operationType);
        }
        return message;
    }
}