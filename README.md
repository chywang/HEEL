# Exploratory Author Name Disambiguation On the Fly for DBLP

### By Chengyu Wang (https://chywang.github.io)

**Introducion:** This software performs exploratory author name disambiguation for author pages in DBLP on-the-fly, which aims at linking ambiguous author names to real authors and discovering new authors without homepages at the same time. The algorithm takes all the publication records related to one author name in DBLP as input. It performs one of the three actions: i) linking the ambiguous author name in a publication record to an existing researcher of the same (or similar) name in DBLP, ii) creating a new author that are not in DBLP, or iii) predicting it as NIL, meaning no confident prediction can be made.

**Paper:** Wang et al. HEEL: Exploratory Entity Linking for Heterogeneous Information Networks. KAIS (accepted)


**APIs**

+ HINMain: The main software entry-point, with three input arguments required.

1. inFile: The input file name of the algorithm, containing a collection of publication records derived from DBLP. Each paper contains an author named "authorName" (the third argument). In this file, each time is a publication record, with items as the DBLP key, title (after stemming), venue, year and author list, separated by tabs. (Refer to the paper for processing details)

> NOTE: The DBLP data dump can be downloaded via https://dblp.org/xml/. Existing linked authors with ambiguous names end in a four-digit number in DBLP, e.g., Hui Fang 0001, Hui Fang 0002, etc.

2. outFile: The output file name of the algorithm. After running the algorithm, unlinked authors in these publication records will be linked to one of the existing authors or new authors.

> NOTE: New authors are numbered with a five-digit number, e.g., Hui Fang 00001, Hui Fang 00002, etc.

3. authorName: The ambiguous author name to be processed.

> NOTE: The default values can be set as: "input.txt", "output.txt" and "Hui Fang".


**Dependencies**

1. This software is run in the JaveSE-1.8 environment. With a large probability, it runs properly in other versions of JaveSE as well. However, there is no guarantee.

**More Notes on the Algorithm** 

This is the light-weight implementation of the PC-EM (Partial Classification Expectation Maximum) algorithm proposed in our paper. Because the HIN (Heterogeneous Information Network) constructed by the entire DBLP dump is too large, we run PC-EM on a tiny "HIN" constructed using the inputs in this on-the-fly version and perform exploratory author name disambiguation. You can also modify the parameters in the source codes. For more data and resources, please contact the authors directly.

**Citation**

If you find this software useful for your research, please cite the following paper.

> @article{kais2019,<br/>
&emsp;&emsp; author = {Chengyu Wang and Xiaofeng He and Aoying Zhou},<br/>
&emsp;&emsp; title = {Heel: exploratory entity linking for heterogeneous information networks},<br/>
&emsp;&emsp; journal = {Knowledge and Information Systems},<br/>
&emsp;&emsp; year = {2019}<br/>
}

More research works can be found here: https://chywang.github.io.



