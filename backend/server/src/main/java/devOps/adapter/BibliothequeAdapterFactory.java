package devOps.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BibliothequeAdapterFactory {

    private final List<LibraryAdapter> adapters;

    @Autowired
    public BibliothequeAdapterFactory(List<LibraryAdapter> adapters) {
        this.adapters = adapters;
    }

    public List<LibraryAdapter> getAllAdapters() {
        return adapters;
    }

    public LibraryAdapter getAdapter(String nomBibliotheque) {
        return adapters.stream()
                .filter(adapter -> adapter.getNomBibliotheque().equalsIgnoreCase(nomBibliotheque))
                .findFirst()
                .orElse(null);
    }
}
