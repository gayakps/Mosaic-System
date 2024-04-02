package gaya.pe.kr.mosaicsystem.infra.repository;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public abstract class AbstractRepository<K,V> {

    HashMap<K,V> hashMap;

    public AbstractRepository(int hashMapSize) {
        hashMap = new HashMap<>(hashMapSize);
    }

    public AbstractRepository(HashMap<K, V> hashMap) {
        this.hashMap = hashMap;
    }

    public AbstractRepository() {
        hashMap = new HashMap<>();
    }

    public void addValue(K k, V v) {
        hashMap.put(k,v);
    }

    public Optional<V> getValue(K k) {
        return Optional.ofNullable(getHashMap().get(k));
    }

    public void removeValue(V v) {

        K removeTargetKey = null;

        for (Map.Entry<K, V> kvEntry : hashMap.entrySet()) {
            K k = kvEntry.getKey();
            V value = kvEntry.getValue();

            if ( value.equals(v) ) {
                removeTargetKey = k;
                break;
            }
        }

        if ( removeTargetKey != null ) {
            hashMap.remove(removeTargetKey);
        }

    }



}
