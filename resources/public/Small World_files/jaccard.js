// Compiled by ClojureScript 1.10.520 {}
goog.provide('clj_fuzzy.jaccard');
goog.require('cljs.core');
goog.require('clojure.set');
/**
 * Compute the Jaccard index between [set1] and  [set2].
 */
clj_fuzzy.jaccard.index = (function clj_fuzzy$jaccard$index(set1,set2){
return (cljs.core.count.call(null,clojure.set.intersection.call(null,set1,set2)) / cljs.core.count.call(null,clojure.set.union.call(null,set1,set2)));
});
/**
 * Compute the Jaccard distance between [seq1] and [seq2].
 */
clj_fuzzy.jaccard.distance = (function clj_fuzzy$jaccard$distance(seq1,seq2){
var set1 = cljs.core.set.call(null,seq1);
var set2 = cljs.core.set.call(null,seq2);
return ((1) - clj_fuzzy.jaccard.index.call(null,set1,set2));
});

//# sourceMappingURL=jaccard.js.map?rel=1642872176879
