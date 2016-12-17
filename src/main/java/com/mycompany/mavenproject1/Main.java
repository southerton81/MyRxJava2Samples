package com.mycompany.mavenproject1;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import java.util.ArrayList;
import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        Observable<List<String>> observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter oe) throws Exception {
                List<String> f = new ArrayList();
                List<String> s = new ArrayList();
                f.add("1");
                f.add("2");
                s.add("3");
                oe.onNext(f);
                oe.onNext(s);
                oe.onComplete();
            }
        });

        observable
                .flatMap(l -> {
                    return Observable.fromIterable(l);
                })
                .subscribe(x -> {
                    System.out.println(x.toString());
                }, error -> {
                    System.out.println(error.toString());
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                          System.out.println("OnCompleted");
                    }
                });
    }
}
