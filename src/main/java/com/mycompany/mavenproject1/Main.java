package com.mycompany.mavenproject1;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        flatMapSample();
        mergeSample();
        zipSample();
        concatMapSample();

    }

    private static void flatMapSample() {
        System.out.println("flatMapSample");
        
        getFirstObservable()
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

    private static void mergeSample() {
        System.out.println("mergeSample");

        Observable.merge(Arrays.asList(getFirstObservable(), getSecondObservable()))
                .subscribe(x -> {
                    System.out.println(x.toString());
                });
    }

    private static void zipSample() {
        System.out.println("zipSample");

        Observable.zip(getFirstObservable(), getSecondObservable(),
                new BiFunction<List<String>, List<String>, Collection<List<String>>>() {
            @Override
            public Collection<List<String>> apply(List<String> t1, List<String> t2) throws Exception {
                Collection<List<String>> zipped = Arrays.asList(t1, t2);
                return zipped;
            }
        })
                .subscribe(x -> {
                    System.out.println(x.toString());
                });
    }

    private static Observable<List<String>> getFirstObservable() {
        Observable<List<String>> observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter oe) throws Exception {
                List<String> f = Arrays.asList("1", "2");
                List<String> s = Arrays.asList("3");
                oe.onNext(f);
                oe.onNext(s);
                oe.onComplete();
            }
        });

        return observable;
    }
   
    private static Observable<List<String>> getSecondObservable() {
        Observable<List<String>> observable2 = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter oe) throws Exception {
                List<String> f = Arrays.asList("4", "5");
                oe.onNext(f);
                oe.onComplete();
            }
        });
        return observable2;
    }

    private static void concatMapSample() {
        System.out.println("concatMapSample");
        
        Observable observable = Observable.fromIterable(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        Scheduler scheduler = Schedulers.from(executorService);

        System.out.println("flatMap does not respect original order -->");
        observable
                .flatMap(l -> {
                    return Observable.just(l).subscribeOn(scheduler);
                })
                .observeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
                .blockingSubscribe(x -> {
                    System.out.println(x.toString());
                });
        
        System.out.println("concatMap does -->");
        observable
                .concatMap(l -> {
                    return Observable.just(l).subscribeOn(scheduler);
                })
                .observeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
                .subscribe(x -> {
                    System.out.println(x.toString());
                });

    }
}
