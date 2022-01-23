// Compiled by ClojureScript 1.10.520 {}
goog.provide('clj_fuzzy.helpers');
goog.require('cljs.core');
goog.require('clojure.string');
/**
 * Slice a [string] from [start] and up to [length].
 */
clj_fuzzy.helpers.slice = (function clj_fuzzy$helpers$slice(string,start,length){
var offset = (((start < (0)))?(cljs.core.count.call(null,string) + start):start);
return cljs.core.apply.call(null,cljs.core.str,cljs.core.take.call(null,length,cljs.core.drop.call(null,offset,string)));
});
/**
 * Drop the last character of a [string].
 */
clj_fuzzy.helpers.chop = (function clj_fuzzy$helpers$chop(string){
return cljs.core.subs.call(null,string,(0),(cljs.core.count.call(null,string) - (1)));
});
/**
 * Drop the first letter of a [string].
 */
clj_fuzzy.helpers.eat = (function clj_fuzzy$helpers$eat(string){
return cljs.core.apply.call(null,cljs.core.str,cljs.core.drop.call(null,(1),string));
});
/**
 * Apply several [replacements] to a [string].
 */
clj_fuzzy.helpers.batch_replace = (function clj_fuzzy$helpers$batch_replace(string,replacements){
var replacement_list = cljs.core.partition.call(null,(2),replacements);
return cljs.core.reduce.call(null,((function (replacement_list){
return (function (p1__29901_SHARP_,p2__29902_SHARP_){
return cljs.core.apply.call(null,clojure.string.replace,p1__29901_SHARP_,p2__29902_SHARP_);
});})(replacement_list))
,string,replacement_list);
});
/**
 * Drop every non alphabetical character in [word].
 */
clj_fuzzy.helpers.clean_non_alphabetical = (function clj_fuzzy$helpers$clean_non_alphabetical(word){
return clojure.string.replace.call(null,word,/[^a-zA-Z]/,"");
});
/**
 * Test a [string] against a [regular-expression].
 */
clj_fuzzy.helpers.re_test_QMARK_ = (function clj_fuzzy$helpers$re_test_QMARK_(regular_expression,string){
return (!((cljs.core.re_find.call(null,regular_expression,string) == null)));
});
/**
 * Drop consecutive duplicates in sequence
 */
clj_fuzzy.helpers.distinct_consecutive = (function clj_fuzzy$helpers$distinct_consecutive(sequence){
return cljs.core.map.call(null,cljs.core.first,cljs.core.partition_by.call(null,cljs.core.identity,sequence));
});
/**
 * Lazily compute the n-grams of a sequence.
 */
clj_fuzzy.helpers.n_grams = (function clj_fuzzy$helpers$n_grams(n,s){
return cljs.core.partition.call(null,n,(1),s);
});
clj_fuzzy.helpers.bigrams = (function clj_fuzzy$helpers$bigrams(s){
return clj_fuzzy.helpers.n_grams.call(null,(2),s);
});
clj_fuzzy.helpers.trigrams = (function clj_fuzzy$helpers$trigrams(s){
return clj_fuzzy.helpers.n_grams.call(null,(3),s);
});
clj_fuzzy.helpers.quadrigrams = (function clj_fuzzy$helpers$quadrigrams(s){
return clj_fuzzy.helpers.n_grams.call(null,(4),s);
});
/**
 * Is any of the [coll] item true according to the given [predicate]?
 */
clj_fuzzy.helpers.any_QMARK_ = (function clj_fuzzy$helpers$any_QMARK_(pred,coll){
return cljs.core.boolean$.call(null,cljs.core.some.call(null,pred,coll));
});
/**
 * Checks whether a [string] is contained within a [sequence].
 */
clj_fuzzy.helpers.in_QMARK_ = (function clj_fuzzy$helpers$in_QMARK_(string,sequence){
return cljs.core.boolean$.call(null,cljs.core.some.call(null,cljs.core.PersistentHashSet.createAsIfByAssoc([string]),sequence));
});
clj_fuzzy.helpers.not_in_QMARK_ = cljs.core.complement.call(null,clj_fuzzy.helpers.in_QMARK_);

//# sourceMappingURL=helpers.js.map?rel=1642872176887
