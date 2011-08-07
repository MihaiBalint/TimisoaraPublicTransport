package ro.mihai.tpt.test;

import ro.mihai.tpt.JavaCityLoader;
import ro.mihai.tpt.RATT;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.NamedEntityCollection;
import ro.mihai.util.NullMonitor;
import junit.framework.TestCase;

/**
 * This file is mostly generated. It's sole purpose is to identify changes 
 * within the RATT web site: line/station renaming or changing IDs or adding new/removing old lines/stations.
 * @author mihai
 *
 */
public class Regressions extends TestCase {
	private static City c = null;
	
	protected synchronized void setUp() throws Exception {
		super.setUp();
		if(c==null) {
			c = RATT.downloadCity(new NullMonitor());
			// c = JavaCityLoader.loadCachedCityOrDownloadAndCache();
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	
	public void testLineCount() {
		assertEquals(58, c.getLines().size());
	}
	public void testLineNames() {
		assertEquals(
			"13 21 26 26a 26b 28 3 32 32a 33 3a 40 44 44a 46 5 Ab33 E1 E2 E3 E4 E5 E6 E7 E7b E8 M22 M22a M22b M24 M27 M30 M35 M36 P1-a P1-d P2-a P2-d P3 P4-a P4-d Tb11 Tb14 Tb15 Tb16 Tb17 Tb18 Tb19 Tv1 Tv2 Tv3b Tv4 Tv5 Tv6 Tv7a Tv7b Tv8 Tv9 ", 
			NamedEntityCollection.sortedNames(c.getLines())
		);
	}
	public void testLineIDs() {
		assertEquals(
			"1006 1046 1066 1086 1106 1126 1146 1166 1186 1206 1207 1226 1266 1286 1326 1346 1366 1406 1526 1546 1547 1548 1550 1551 1552 1553 1556 1557 1558 1646 1686 1746 1766 1846 1866 1886 1906 1926 1928 1946 1966 1986 2006 2026 2086 2106 2126 2146 2166 2186 2207 2226 2246 2286 2306 886 989 990 ", 
			NamedEntityCollection.sortedIDs(c.getLines())
		);
	}

	
	public void testStationCount() {
		assertEquals(597, c.getStations().size());
	}
	public void testStationNames() {
		assertEquals(
			"A Saguna  A.Guttenbrun_1 A.Guttenbrun_2 A.Lapusneanu_1 A.Lapusneanu_2 A.Saguna 1a A.Saguna 1tb AT_Gara de Nord_1 AT_Miresei_1 AT_Miresei_2 AT_Pop de Basesti_1 AT_Pop de Basesti_2 AT_Reg.Carol_1 AT_Reg.Carol_2 AT_Torontalului_2 Ab-Pod C.Sagului 2 Ab_B.Cartan_0 Ab_B.Cartan_1 Ab_Brancoveanu 1 Ab_Brancoveanu_ Ab_Bujorilor_ Ab_C.Europei 1 Ab_C.Europei 2 Ab_C.Europei 3 Ab_Catedrala 1 Ab_Catedrala 2 Ab_Cvar.UMT_0 Ab_Cvar.UMT_1 Ab_Dambovita Ab_Dambovita_ Ab_Gara de Est_0 Ab_Gara de Est_1 Ab_Gh.Lazar 1 Ab_Izlaz_ Ab_P-ta I.Maniu 1 Ab_P-ta I.Maniu 3 Ab_Pod C.Sagului 1 Ab_Selgros 1 Ab_Selgros 2 Ab_U.M.T._0 Ab_U.M.T_1 Abator Abator p Abator plecare Abator s Aeroport Aeroport Civil Aeroport Civil_II Agronomie 1tb Agronomie 2tb Albastrelelor 1a Albastrelelor 2a Albastrelelor1 Aleea Pad.V Aleea Pad.V_ Apateu 1a Apateu1 Apicultorilor 1a Arena Aqua 1tb Arena Aqua 2tb B Cartan_II B.A.T.M.A 2 a  B.A.T.M.A. 1 a Baba Dochia  Badea Cartan 1tb Badea Cartan 2tb Balcescu Balcescu_ Balcescu_1 Balcescu_2 BaltaVerde 3 BaltaVerde 4 Banatim Banatim_3 Banatim_4 Banatul_1 Banatul_2 Bastion Bastion s Bastion_s Bobalna  Bobalna 1a Brancoveanu Brancoveanu.. Brancoveanu_ Brancoveanu_2 Brediceanu_1 Brediceanu_2 Bucla Urseni Bujorilor Bujorilor_ Bv L Rebreanu_ Bv Sudului_1 Bv Sudului_2 Bv. Cetatii 1tb Bv. Cetatii 2tb Bv. Sudului 1tb Bv.Sudului(bucla) Bv.Sudului(bucla) 2tb C Popescu 1a C Terra C Torontalului 2a C Torontalului 2a C Torontalului 2a C Torontalului 2a C.E.T. 1a C.I.Nottara_1 C.I.Nottara_2 C.Martirilor_1 C.Martirilor_2 C.P.Solventul 1tb. C.P.Solventul 2tb Campului Canton C.F.R_1 Canton C.F.R_2 Caprioarei 1 Caprioarei 1a Carabusului Carmen Silva Cartier Aeroport Cartier Aeroport II Cefin 1a Chisodei Chisodei Chisodei_ Chisodei_ Cim. Eroilor 1tb Cim.Eroilor 2tb Circumvalat. 1tb Circumvalat.2tb Circumvalatiunii Circumvalatiunii 2a Cluj 1tb. Cluj 2tb. Comp. Studentesc 1tb Comp. Studentesc 2tb Complex Euro Cv.Sudului 1tb Cv.Sudului 2tb DEP.TROLEIBUZE DN6 DN6_II Dacia Service 2tb Dambovita 1a Dambovita 2a Dambovita_3 Dambovita_4 Dambovita_5 Dambovita_5_ Deliblata_1 Deliblata_2 Dep trl.iesire Depou Tache Ionescu Depoul 1 TV Depoul 2 Tv Dermatina  Dermatina_ Div 9 Cavalerie Div 9 Cavalerie 2a Div.9 Cavalerie 1tb Domasnean_3 Domasnean_4 Drubeta _3 Drubeta_1 Drubeta_2 Drubeta_4 Dudesti_Centru E.Zola E.Zola_ Eternitatii Eternitatii_ F-ca de Zahar_1 F-ca de Zahar_2 FM Logistic Fac Electro 1a Fac Electro_ Fac de Mecanica_ Fac. de Mecanica Ferma 6 1a Ferma 6 1a Ferma 6 2a Ferma 6 2a G Vii Complex1 G Vii Complex1_ G.Alexandrescu 1tb G.Alexandrescu 2tb G.Alexandrescu_1 G.Alexandrescu_2 Gara de Est 1a Gara de Nord Gara de Nord 2tb Gara de Sud  Garaj Auto Gelu 1tb. Gelu 2tb. Gh.Domasnean Gh.Domasnean_ Gh.Lazar Gh.Ranetti_1 Gh.Ranetti_2 Giarmata Vii 1 Giarmata Vii 2p Giarmata Vii 2s Giarmata Vii1_ H Continental Hector Hella Hella_ Herculane Herculane_ Herodot 1a Herodot1 Holdelor 1a I.C.Bratianu  I.I.de la Brad 1tb. I.I.de la Brad 2tb. ILSA 1tb. Ialomita Ialomita_ Ialomitei Ianova 1 Ianova 1_ Ianova 2S Ianova 2p Iepurelui Iepurelui_ Iokai Mor Iosif Vucan  Iosif Vulcan1 Ismail Iulius Mall 1tb. Iulus Mall 1a Iulus Mall 2a Izlaz Izlaz_ Jiul 1tb. Jiul 2tb. L. Rebreanu_ L.Rebreanu  L.Rebreanu_1 L.Rebreanu_2 L.Rebreanu_2 La Blocuri 1 La Blocuri 2 La Moara 1 La Moara 2 La Negrea La Roman 1 Lalelelor  Levantica 1a Levantica1 Lic L. Calderon Lic L. Calderon_ Lic. Auto_ Liceul Auto Liceul Auto_1 Liceul Auto_2 Lidia Lidia_ Liege Liege 1tb Liege 2a Liege 2tb. Liege 3tb Liege_2 Lugojului Lugojului_ Luncani 1a M.C-tin Prezan 1tb. M.C-tin Prezan 2tb. M.Eminescu 1tb Macilor_1 Macilor_2 Madona_1 Madona_2 Mangalia 1a Mangalia 2a Mart.I.Stanciu 1tb. Mart.I.Stanciu 2tb. Martirilor 1tb. Martirilor 2tb. Matasarilor  Matasarilor 1a Memorandului Memorandului_ Metro 1a Metro 1b Metro 2 Metro_2 Miresei_2 Moldovei  Moldovei 1a Mosnita Noua1 Mosnita Noua1_ Mosnita Noua2 Mosnita Noua2_ Mosnita Noua3 Mosnita Noua3_ Mosnita Noua4 Mosnita Noua4_ Mosnita Veche Moteletul Moteletul_ Mures Mures Mures Mures_ Muzeul Satului Muzicescu Muzicescu Nume Olimpia_1 Olimpia_2 Opre Gogu_1 Opre Gogu_2 Otniel Otniel_ P-ta 700 1tb. P-ta 700 2tb P-ta 700 3tb P-ta 700 4tb P-ta A.Iancu 1tb P-ta A.Iancu 2tb P-ta C.Europei 1tb P-ta C.Europei 2tb P-ta Crucii_1 P-ta Crucii_2 P-ta I.Maniu P-ta L. da Vinci  P-ta Mocioni P-ta Mocioni_ P-ta P Sandor 1a P-ta Traian p P-ta de Gros 1tb P-ta de Gros 2tb PITT Pacii Pacii 1a Pacii 2a Pacii_ Parc Doina_1 Parc Doina_2 Parc Logistic Parc Logistic_ Pasaj C.F. Pavlov Pavlov_ Peco 1a Peco 2a Pestalozzi_ Petru si Pavel 1a Petru si Pavel 1tb Petru si Pavel 2tb Pindului  Pindului1 Pod C. Sagului Pod Ghiroda Pod Ghiroda s Pod M Saguna 1a Pod Sanm.R Pod Sanm.R_ Podgoriei Podgoriei_ Polona Polona_1 Polona_2 Pomiculturii 1a Pomiculturii 1tb Pop de Basesti 1tb Pop de Basesti 2tb. Popa Sapca 1a Popa Sapca 2a Porumbescu Porumbescu_ Prefatim Prefatim_ Prezan (bucla) Prieteniei Prieteniei  Primaria Ghiroda Primaria Ghiroda_ Primarie SR Primarie SR_ Progresul Progresul_ R.Carol Rampa Colonistilor Rampa Colonistilor_ Rascoala 1907_1 Rascoala 1907_2 Razboieni_1 Razboieni_2 Real 3 Real 3_ Real Practiker Real Practiker_ Regele Carol_3 Regina Maria 1a Regina Maria 1tb Regina Maria 2tb Remetea Mare 1 Remetea Mare 1_ Remetea Mare 2 Remetea Mare 2_ Remetea Mare 3 Remetea Mare 3_ Remus Radulet 1tb Renasterii 1tb. Renasterii 2tb. Ronat_1 Ronat_2 Rudolf Walter 1a Rudolf Walter 2a Salcamilor_1 Salcamilor_2 Samuil Micu 1tb Samuil Micu 2tb Sanmh R_Peco Sanmh R_Peco_ Sanmihaiu G Sanmihaiu G_ Scoala Plopi 1a Snagov_ Sp Judetean_1 Sp Judetean_2 Sp.Judetean Sp.Judetean 2tb Spartacus  Stefan Cel Mare  Stefan cel Mare1 Str.Cluj_1 Str.Cluj_2 Strand Termal Strand Termal_ Stuparilor 1a Stuparilor 2a T.Grozavescu  T.Grozavescu 1a T.Grozavescu 2a T.Grozavescu p Tb_A.Popovici 2 Tb_Baader 1 Tb_Bastion 1 Tb_Gara  Nord 1 Tb_Ghe.Baritiu 1 Tb_Ghe.Baritiu 2 Tb_Oituz 2 Tb_P-ta Marasti 1 Tb_P-ta Marasti 2 Tb_Pol.Jud.Timis 1 Tb_Pol.Jud.Timis 2. Timis 1tb Timis 2tb Titeica Titeica 1a Torac  Torac1 Torontalului 1tb Transilvania_1 Transilvania_2 Transilvania_3 Tv-Mures Tv-Mures_ Tv_3 August 1 Tv_3 August 2 Tv_Amforei 1 Tv_Amforei 2 Tv_B.Cartan 1 Tv_B.Cartan 2 Tv_BaltaVerde 1 Tv_BaltaVerde 2 Tv_Banatim 1 Tv_Banatim 2 Tv_Catedrala 1 Tv_Catedrala 2 Tv_Cetatii  2 Tv_Cetatii 1 Tv_Ciarda R 1 Tv_Ciarda R 2 Tv_Circumv 1 Tv_Circumv 2 Tv_Continental 1 Tv_Continental 2 Tv_Crizantem 2 Tv_Crizantem_1 Tv_Dambovita 1 Tv_Dambovita 2 Tv_Detergenti 1 Tv_Detergenti 2 Tv_Domasnean 1 Tv_Domasnean 2 Tv_Economu 1 Tv_Economu 2 Tv_Electrica 1 Tv_Electrica 2 Tv_Electrotim 1 Tv_Electrotim 2 Tv_F de Bere 1 Tv_F de Bere 2 Tv_Gara  Est 1 Tv_Gara  Est 2 Tv_Gara  Nord 1 Tv_Gara  Nord 2 Tv_I Maniu 1 Tv_I Maniu 2 Tv_IOT 1 Tv_IOT 2 Tv_Lalelelor 1 Tv_Lalelelor 2 Tv_Libertatii 1 Tv_Libertatii 2 Tv_M.Basarab 1 Tv_M.Basarab 2 Tv_Mangalia 1 Tv_Mangalia 2 Tv_Mecatim 1 Tv_Mecatim 2 Tv_Mendeleev 1 Tv_Mendeleev 2 Tv_Meteo 1 Tv_Meteo 2 Tv_Mocioni 1 Tv_Mocioni 2 Tv_P Turcesc 1 Tv_P Turcesc 2 Tv_P-ta 700 1 Tv_P-ta 700 2 Tv_P-ta Maria 1 Tv_P-ta Maria 2 Tv_P-ta Maria 3 Tv_P-ta Traian 1 Tv_P-ta Traian 2 Tv_P-ta Traian pl Tv_Prefectura 1 Tv_Prefectura 2 Tv_R Carol 1 Tv_R Carol 2 Tv_Sarmiseg 1 Tv_Sarmiseg 2 Tv_Spumotim 1 Tv_Spumotim 2 Tv_Torontal 1 Tv_Torontal 2 Tv_U.M.T - gara Tv_U.M.T 1 Tv_V.Babes 1 Tv_V.Babes 2 U.M.T._2 plecare U.M.T_2 U.T.T. 1tb U.T.T. 2tb UM Aeroport UM Aeroport II UZINA DE APA Univ D. Cantemir_ Univ de Vest  Univ de Vest_ Urseni Urseni_ Utvin Biserica Utvin Biserica_ Utvin Pl Ind Utvin Pl Ind_ Utvin balta Utvin balta_ Uzina de apa Uzina de apa 2a Uzinei Uzinei 1a Uzinei 2 a Uzinei 3a V Carlova 2a V Parvan 2a V Simionescu 1a V. Parvan V.Carlova1 V.Economu 1tb V.Economu 2tb V.Simionescu 2a Valisoara 1 a Valisoara 2 a Valisoara 2a Vaslui 1a Vaslui 2a Veteranilor Veteranilor. Veteranilor.. Veteranilor_ Victor Hugo Victor Hugo_ Vuk Karadjic 1tb Vuk Karadjic 2tb Zalau_1 Zalau_2 locatie str. Corbului str.Corbului ", 
			NamedEntityCollection.sortedNames(c.getStations())
		);
	}
	public void testStationIDs() {
		assertEquals(
			"2650 2652 2653 2654 2655 2656 2659 2660 2661 2662 2663 2664 2665 2666 2667 2668 2669 2671 2672 2673 2675 2679 2680 2682 2683 2684 2685 2687 2690 2691 2694 2695 2701 2702 2703 2704 2705 2706 2707 2708 2710 2713 2714 2715 2716 2717 2718 2719 2720 2722 2723 2724 2726 2727 2728 2729 2730 2732 2733 2734 2735 2736 2738 2739 2740 2741 2745 2747 2748 2749 2752 2753 2754 2755 2756 2757 2759 2760 2761 2763 2764 2765 2766 2767 2768 2772 2773 2781 2784 2793 2794 2798 2799 2800 2806 2807 2808 2809 2810 2812 2813 2814 2815 2816 2818 2819 2820 2821 2822 2823 2824 2826 2827 2828 2829 2830 2831 2832 2833 2836 2837 2838 2839 2840 2841 2842 2843 2844 2845 2846 2847 2880 2881 2882 2883 2884 2885 2886 2887 2888 2889 2920 2921 2922 2923 2924 2925 2926 2927 2928 2929 2945 2946 2947 2948 2949 2950 2951 2952 2953 2954 2955 2956 2957 2960 2961 2962 2963 2964 2965 2966 2967 2968 2971 2972 2973 2974 2975 2976 2977 2979 2980 3000 3002 3003 3004 3005 3006 3007 3008 3011 3012 3013 3014 3015 3016 3017 3018 3019 3020 3021 3022 3041 3060 3080 3102 3103 3104 3105 3106 3107 3120 3160 3161 3163 3164 3165 3166 3167 3168 3169 3170 3171 3172 3173 3174 3175 3176 3177 3181 3200 3220 3221 3224 3225 3226 3240 3241 3243 3244 3245 3247 3248 3249 3250 3253 3254 3255 3260 3280 3281 3340 3361 3362 3400 3401 3402 3403 3404 3405 3406 3408 3410 3411 3412 3413 3420 3461 3481 3482 3483 3500 3501 3503 3505 3506 3507 3508 3520 3540 3542 3543 3544 3545 3546 3547 3548 3549 3550 3551 3552 3553 3554 3556 3560 3561 3563 3564 3565 3566 3567 3568 3569 3571 3572 3573 3574 3580 3581 3582 3583 3584 3585 3586 3587 3588 3589 3590 3591 3592 3593 3594 3595 3596 3597 3598 3599 3600 3601 3602 3620 3640 3641 3642 3643 3644 3645 3646 3647 3648 3649 3650 3651 3652 3653 3660 3661 3662 3663 3664 3680 3681 3682 3683 3684 3685 3686 3701 3720 3741 3743 3780 3781 3820 3821 3840 3860 3861 3862 3880 3881 3920 4000 4020 4021 4022 4041 4042 4060 4100 4300 4301 4320 4321 4322 4323 4324 4325 4341 4420 4421 4424 4427 4428 4429 4431 4462 4463 4464 4465 4483 4484 4486 4490 4491 4492 4493 4494 4496 4501 4502 4503 4504 4580 4581 4582 4583 4640 4680 4700 4701 4720 4760 4780 4800 4801 4820 4840 4860 4880 4920 4980 4981 5000 5001 5020 5021 5040 5041 5060 5061 5080 5081 5100 5101 5120 5140 5141 5160 5180 5181 5200 5220 5240 5260 5280 5281 5282 5283 5300 5320 5321 5340 5360 5361 5380 5400 5420 5440 5460 5480 5481 5500 5501 5520 5540 5580 5600 5601 5620 5640 5641 5642 5660 5661 5680 5681 5700 5701 5702 5720 5721 5740 5741 5760 5761 5780 5800 5820 5840 5841 5842 5860 5920 5940 5960 5961 5962 5963 5964 5965 5980 6000 6001 6003 6020 6021 6022 6023 6024 6025 6026 6027 6040 6041 6100 6120 6121 6140 6141 6142 6144 6145 6146 6160 6180 6181 6200 6240 6241 6260 6800 6820 6840 6841 6860 6861 6880 6900 6940 6941 6942 6943 6960 6961 6962 6980 7000 7020 7040 7041 7060 7061 7062 7063 7064 7065 7066 7067 7068 7080 7100 7101 7120 7121 7140 7160 7180 7200 7201 7220 7221 7240 7241 7260 7280 7283 7340 7341 7342 7343 7345 7382 7480 7500 7540 7560 ", 
			NamedEntityCollection.sortedIDs(c.getStations())
		);
	}
	
	
	
	public void testLine_13_StationCount() {
		assertEquals(18, c.getLine("13").getStations().size());
	}
	public void testLine_13_StationNames() {
		assertEquals("Bv. Cetatii 1tb Bv. Cetatii 2tb Circumvalat. 1tb Circumvalat.2tb G.Alexandrescu 1tb G.Alexandrescu 2tb P-ta 700 3tb P-ta 700 4tb P-ta A.Iancu 1tb P-ta A.Iancu 2tb P-ta de Gros 1tb P-ta de Gros 2tb Pasaj C.F. Tb_P-ta Marasti 1 Timis 1tb Timis 2tb Vuk Karadjic 1tb Vuk Karadjic 2tb ",
			 NamedEntityCollection.sortedNames(c.getLine("13").getStations())
		);
	}
	public void testLine_13_StationIDs() {
		assertEquals("3007 3008 3011 3012 3013 3014 3015 3016 3017 3018 3019 3020 3021 3022 3060 3412 3413 5200 ",
			 NamedEntityCollection.sortedIDs(c.getLine("13").getStations())
		);
	}
	public void testLine_21_StationCount() {
		assertEquals(28, c.getLine("21").getStations().size());
	}
	public void testLine_21_StationNames() {
		assertEquals("A Saguna  Albastrelelor 1a Albastrelelor1 Apateu 1a Apateu1 Baba Dochia  C Popescu 1a Caprioarei 1 Caprioarei 1a Iepurelui Iepurelui_ Iokai Mor Iosif Vucan  Iosif Vulcan1 Levantica 1a Levantica1 Pindului  Pindului1 Rudolf Walter 1a Rudolf Walter 2a Scoala Plopi 1a Stefan Cel Mare  Stefan cel Mare1 Torac1 Tv_F de Bere 1 Tv_F de Bere 2 Uzina de apa V.Carlova1 ",
			 NamedEntityCollection.sortedNames(c.getLine("21").getStations())
		);
	}
	public void testLine_21_StationIDs() {
		assertEquals("2828 2839 2841 2842 2846 2847 3400 3401 3402 3403 3405 3406 3408 3411 3420 3554 3556 4424 6940 6941 6943 6960 6961 6962 6980 7000 7240 7241 ",
			 NamedEntityCollection.sortedIDs(c.getLine("21").getStations())
		);
	}
	public void testLine_26_StationCount() {
		assertEquals(12, c.getLine("26").getStations().size());
	}
	public void testLine_26_StationNames() {
		assertEquals("Aeroport Civil Badea Cartan 1tb Badea Cartan 2tb Bastion Cartier Aeroport Cartier Aeroport II DN6 DN6_II Tb_A.Popovici 2 UM Aeroport V.Economu 1tb V.Economu 2tb ",
			 NamedEntityCollection.sortedNames(c.getLine("26").getStations())
		);
	}
	public void testLine_26_StationIDs() {
		assertEquals("2968 2971 2972 3103 3104 3820 3821 3840 3860 3880 3881 5940 ",
			 NamedEntityCollection.sortedIDs(c.getLine("26").getStations())
		);
	}
	public void testLine_26a_StationCount() {
		assertEquals(8, c.getLine("26a").getStations().size());
	}
	public void testLine_26a_StationNames() {
		assertEquals("Aeroport Civil Aeroport Civil_II B Cartan_II Badea Cartan 1tb DN6 DN6_II V.Economu 1tb V.Economu 2tb ",
			 NamedEntityCollection.sortedNames(c.getLine("26a").getStations())
		);
	}
	public void testLine_26a_StationIDs() {
		assertEquals("2971 2972 3103 3820 3821 3840 3862 3920 ",
			 NamedEntityCollection.sortedIDs(c.getLine("26a").getStations())
		);
	}
	public void testLine_26b_StationCount() {
		assertEquals(10, c.getLine("26b").getStations().size());
	}
	public void testLine_26b_StationNames() {
		assertEquals("B Cartan_II Badea Cartan 1tb Cartier Aeroport Cartier Aeroport II DN6 DN6_II UM Aeroport UM Aeroport II V.Economu 1tb V.Economu 2tb ",
			 NamedEntityCollection.sortedNames(c.getLine("26b").getStations())
		);
	}
	public void testLine_26b_StationIDs() {
		assertEquals("2971 2972 3103 3820 3821 3860 3861 3880 3881 3920 ",
			 NamedEntityCollection.sortedIDs(c.getLine("26b").getStations())
		);
	}
	public void testLine_28_StationCount() {
		assertEquals(18, c.getLine("28").getStations().size());
	}
	public void testLine_28_StationNames() {
		assertEquals("Bobalna  Bobalna 1a Carabusului Ialomitei Ismail Lalelelor  Luncani 1a Matasarilor  Matasarilor 1a Moldovei  Moldovei 1a Prieteniei Prieteniei  Spartacus  Titeica Titeica 1a Uzinei Valisoara 1 a ",
			 NamedEntityCollection.sortedNames(c.getLine("28").getStations())
		);
	}
	public void testLine_28_StationIDs() {
		assertEquals("2650 2652 2653 2654 2655 2656 2661 2663 2664 3481 3505 3506 3507 3508 5440 5460 5480 5481 ",
			 NamedEntityCollection.sortedIDs(c.getLine("28").getStations())
		);
	}
	public void testLine_3_StationCount() {
		assertEquals(19, c.getLine("3").getStations().size());
	}
	public void testLine_3_StationNames() {
		assertEquals("AT_Pop de Basesti_1 AT_Pop de Basesti_2 AT_Reg.Carol_1 AT_Reg.Carol_2 Ab_P-ta I.Maniu 3 Canton C.F.R_1 Canton C.F.R_2 Dambovita 2a Dambovita_3 Dambovita_4 Gara de Nord 2tb Liceul Auto_1 Mangalia 1a Mangalia 2a Opre Gogu_1 Opre Gogu_2 Pacii Pacii_ Tb_Gara  Nord 1 ",
			 NamedEntityCollection.sortedNames(c.getLine("3").getStations())
		);
	}
	public void testLine_3_StationIDs() {
		assertEquals("2768 2810 2821 2822 2889 2920 2921 2922 2923 2925 3106 3602 3620 4464 4465 4501 4502 6040 6041 ",
			 NamedEntityCollection.sortedIDs(c.getLine("3").getStations())
		);
	}
	public void testLine_32_StationCount() {
		assertEquals(22, c.getLine("32").getStations().size());
	}
	public void testLine_32_StationNames() {
		assertEquals("Ab_Brancoveanu 1 Ab_P-ta I.Maniu 1 Brancoveanu.. Bujorilor_ Bv L Rebreanu_ Chisodei Chisodei_ E.Zola E.Zola_ Eternitatii Eternitatii_ Gara de Sud  Herculane Herculane_ L.Rebreanu_2 Mures Mures_ P-ta I.Maniu P-ta Mocioni P-ta Mocioni_ Prefatim Prefatim_ ",
			 NamedEntityCollection.sortedNames(c.getLine("32").getStations())
		);
	}
	public void testLine_32_StationIDs() {
		assertEquals("2666 2667 2668 2691 2694 2695 2705 2707 2713 2714 2715 2717 2719 2723 2730 2733 2734 2736 4421 4820 4840 7540 ",
			 NamedEntityCollection.sortedIDs(c.getLine("32").getStations())
		);
	}
	public void testLine_32a_StationCount() {
		assertEquals(21, c.getLine("32a").getStations().size());
	}
	public void testLine_32a_StationNames() {
		assertEquals("Ab_Brancoveanu 1 Brancoveanu_ Bujorilor_ Bv L Rebreanu_ Chisodei Eternitatii Eternitatii_ Gara de Sud  Herculane Herculane_ L.Rebreanu  Mures Mures_ P-ta Mocioni P-ta Mocioni_ Porumbescu Prefatim Prefatim_ Snagov_ Victor Hugo Victor Hugo_ ",
			 NamedEntityCollection.sortedNames(c.getLine("32a").getStations())
		);
	}
	public void testLine_32a_StationIDs() {
		assertEquals("2666 2694 2695 2701 2703 2705 2707 2714 2717 2719 2723 2730 2733 2736 2739 2740 4421 4820 4840 5180 5181 ",
			 NamedEntityCollection.sortedIDs(c.getLine("32a").getStations())
		);
	}
	public void testLine_33_StationCount() {
		assertEquals(19, c.getLine("33").getStations().size());
	}
	public void testLine_33_StationNames() {
		assertEquals("Ab_Brancoveanu 1 Ab_Catedrala 1 Ab_Catedrala 2 Ab_Dambovita Ab_Dambovita_ Ab_P-ta I.Maniu 1 B.A.T.M.A 2 a  B.A.T.M.A. 1 a Brancoveanu.. Dermatina  Dermatina_ P-ta I.Maniu P-ta Mocioni P-ta Mocioni_ Peco 1a Peco 2a Real Practiker Veteranilor Veteranilor_ ",
			 NamedEntityCollection.sortedNames(c.getLine("33").getStations())
		);
	}
	public void testLine_33_StationIDs() {
		assertEquals("2666 2667 2668 2669 2673 2675 2679 2684 2685 2687 2690 2691 2694 2695 2799 3200 4760 4780 5501 ",
			 NamedEntityCollection.sortedIDs(c.getLine("33").getStations())
		);
	}
	public void testLine_3a_StationCount() {
		assertEquals(21, c.getLine("3a").getStations().size());
	}
	public void testLine_3a_StationNames() {
		assertEquals("Abator s C.I.Nottara_1 C.I.Nottara_2 Canton C.F.R_1 Canton C.F.R_2 Dambovita_3 Dambovita_4 F-ca de Zahar_1 F-ca de Zahar_2 Liceul Auto_1 Liceul Auto_2 Opre Gogu_1 Opre Gogu_2 Pacii Pacii_ Podgoriei Podgoriei_ Polona Polona_1 Rascoala 1907_1 Rascoala 1907_2 ",
			 NamedEntityCollection.sortedNames(c.getLine("3a").getStations())
		);
	}
	public void testLine_3a_StationIDs() {
		assertEquals("2920 2921 2922 2923 2924 2925 2926 2927 2928 2929 2946 2947 2948 3602 3620 5960 5961 5962 5963 6040 6041 ",
			 NamedEntityCollection.sortedIDs(c.getLine("3a").getStations())
		);
	}
	public void testLine_40_StationCount() {
		assertEquals(13, c.getLine("40").getStations().size());
	}
	public void testLine_40_StationNames() {
		assertEquals("C Terra Div 9 Cavalerie Div 9 Cavalerie 2a Gara de Est 1a Holdelor 1a Iulus Mall 1a Iulus Mall 2a Petru si Pavel 2tb Pomiculturii 1a Popa Sapca 1a Popa Sapca 2a Stuparilor 1a T.Grozavescu 1a ",
			 NamedEntityCollection.sortedNames(c.getLine("40").getStations())
		);
	}
	public void testLine_40_StationIDs() {
		assertEquals("2963 3220 3224 3225 3226 3240 3241 3245 3247 3248 3255 3260 3280 ",
			 NamedEntityCollection.sortedIDs(c.getLine("40").getStations())
		);
	}
	public void testLine_44_StationCount() {
		assertEquals(11, c.getLine("44").getStations().size());
	}
	public void testLine_44_StationNames() {
		assertEquals("A Saguna  Campului Caprioarei 1a Herodot 1a Iepurelui_ Iosif Vucan  Levantica 1a Pindului  Stefan Cel Mare  Torac  UZINA DE APA ",
			 NamedEntityCollection.sortedNames(c.getLine("44").getStations())
		);
	}
	public void testLine_44_StationIDs() {
		assertEquals("2839 2841 2842 2843 2844 2845 2846 2847 3400 3411 5420 ",
			 NamedEntityCollection.sortedIDs(c.getLine("44").getStations())
		);
	}
	public void testLine_44a_StationCount() {
		assertEquals(11, c.getLine("44a").getStations().size());
	}
	public void testLine_44a_StationNames() {
		assertEquals("A Saguna  Campului Caprioarei 1a Herodot 1a Iepurelui_ Iosif Vucan  Levantica 1a Pindului  Stefan Cel Mare  Torac  UZINA DE APA ",
			 NamedEntityCollection.sortedNames(c.getLine("44a").getStations())
		);
	}
	public void testLine_44a_StationIDs() {
		assertEquals("2839 2841 2842 2843 2844 2845 2846 2847 3400 3411 5420 ",
			 NamedEntityCollection.sortedIDs(c.getLine("44a").getStations())
		);
	}
	public void testLine_46_StationCount() {
		assertEquals(8, c.getLine("46").getStations().size());
	}
	public void testLine_46_StationNames() {
		assertEquals("Ab_Cvar.UMT_0 Ab_Cvar.UMT_1 Ab_Gara de Est_0 Ab_Gara de Est_1 Ab_U.M.T._0 Ab_U.M.T_1 Badea Cartan 2tb Muzeul Satului ",
			 NamedEntityCollection.sortedNames(c.getLine("46").getStations())
		);
	}
	public void testLine_46_StationIDs() {
		assertEquals("3104 4320 4321 4322 4323 4324 4325 4341 ",
			 NamedEntityCollection.sortedIDs(c.getLine("46").getStations())
		);
	}
	public void testLine_5_StationCount() {
		assertEquals(16, c.getLine("5").getStations().size());
	}
	public void testLine_5_StationNames() {
		assertEquals("A.Guttenbrun_1 A.Guttenbrun_2 A.Lapusneanu_1 A.Lapusneanu_2 BaltaVerde 3 G.Alexandrescu_1 G.Alexandrescu_2 Macilor_1 Macilor_2 Madona_1 Madona_2 Razboieni_1 Razboieni_2 Ronat_1 Ronat_2 Zalau_1 ",
			 NamedEntityCollection.sortedNames(c.getLine("5").getStations())
		);
	}
	public void testLine_5_StationIDs() {
		assertEquals("3640 3641 3642 3643 3644 3645 3646 3647 3648 3649 3650 3651 3652 3653 4800 7220 ",
			 NamedEntityCollection.sortedIDs(c.getLine("5").getStations())
		);
	}
	public void testLine_Ab33_StationCount() {
		assertEquals(19, c.getLine("Ab33").getStations().size());
	}
	public void testLine_Ab33_StationNames() {
		assertEquals("Ab_Brancoveanu 1 Ab_Catedrala 1 Ab_Catedrala 2 Ab_Dambovita Ab_Dambovita_ Ab_P-ta I.Maniu 1 B.A.T.M.A 2 a  B.A.T.M.A. 1 a Brancoveanu.. Dermatina  Dermatina_ P-ta I.Maniu P-ta Mocioni P-ta Mocioni_ Peco 1a Peco 2a Real Practiker Veteranilor Veteranilor_ ",
			 NamedEntityCollection.sortedNames(c.getLine("Ab33").getStations())
		);
	}
	public void testLine_Ab33_StationIDs() {
		assertEquals("2666 2667 2668 2669 2673 2675 2679 2684 2685 2687 2690 2691 2694 2695 2799 3200 4760 4780 5501 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Ab33").getStations())
		);
	}
	public void testLine_E1_StationCount() {
		assertEquals(28, c.getLine("E1").getStations().size());
	}
	public void testLine_E1_StationNames() {
		assertEquals("AT_Gara de Nord_1 AT_Pop de Basesti_1 AT_Pop de Basesti_2 AT_Reg.Carol_1 AT_Reg.Carol_2 Ab-Pod C.Sagului 2 Ab_C.Europei 3 Ab_Dambovita Ab_Dambovita_ Ab_Gh.Lazar 1 Ab_P-ta I.Maniu 3 Ab_Pod C.Sagului 1 Ab_Selgros 1 Ab_Selgros 2 Agronomie 1tb Agronomie 2tb Circumvalatiunii Circumvalatiunii 2a Dacia Service 2tb Gh.Lazar Liege 1tb Liege 2tb. P-ta C.Europei 1tb P-ta I.Maniu Remus Radulet 1tb Tb_Gara  Nord 1 Veteranilor Veteranilor_ ",
			 NamedEntityCollection.sortedNames(c.getLine("E1").getStations())
		);
	}
	public void testLine_E1_StationIDs() {
		assertEquals("2668 2669 2690 2768 2889 2950 2951 2952 2955 2956 2961 2965 4462 4463 4464 4465 4483 4484 4486 4501 4502 4503 4504 4760 4780 6180 6181 7260 ",
			 NamedEntityCollection.sortedIDs(c.getLine("E1").getStations())
		);
	}
	public void testLine_E2_StationCount() {
		assertEquals(18, c.getLine("E2").getStations().size());
	}
	public void testLine_E2_StationNames() {
		assertEquals("Cluj 1tb. Cluj 2tb. Comp. Studentesc 1tb Comp. Studentesc 2tb Complex Euro Cv.Sudului 1tb Cv.Sudului 2tb Div.9 Cavalerie 1tb Gh.Domasnean H Continental Hector Iulius Mall 1tb. P-ta C.Europei 2tb Petru si Pavel 1a Pomiculturii 1tb Sp.Judetean Sp.Judetean 2tb Stuparilor 2a ",
			 NamedEntityCollection.sortedNames(c.getLine("E2").getStations())
		);
	}
	public void testLine_E2_StationIDs() {
		assertEquals("2716 2718 2784 2964 2966 2979 2980 3000 3003 3004 3250 3281 4427 4428 4860 5220 5240 5260 ",
			 NamedEntityCollection.sortedIDs(c.getLine("E2").getStations())
		);
	}
	public void testLine_E3_StationCount() {
		assertEquals(21, c.getLine("E3").getStations().size());
	}
	public void testLine_E3_StationNames() {
		assertEquals("Ab_Catedrala 2 Apicultorilor 1a Carmen Silva Cluj 1tb. Cluj 2tb. Comp. Studentesc 1tb Comp. Studentesc 2tb Gara de Nord 2tb Jiul 1tb. Jiul 2tb. Mart.I.Stanciu 1tb. Mart.I.Stanciu 2tb. Martirilor 1tb. Martirilor 2tb. Mures Mures Muzicescu Muzicescu Tb_Gara  Nord 1 Univ de Vest  Univ de Vest_ ",
			 NamedEntityCollection.sortedNames(c.getLine("E3").getStations())
		);
	}
	public void testLine_E3_StationIDs() {
		assertEquals("2716 2718 2720 2722 2724 2726 2768 2772 2773 2807 2826 2979 2980 3106 3200 4429 5280 5281 5282 5283 5300 ",
			 NamedEntityCollection.sortedIDs(c.getLine("E3").getStations())
		);
	}
	public void testLine_E4_StationCount() {
		assertEquals(15, c.getLine("E4").getStations().size());
	}
	public void testLine_E4_StationNames() {
		assertEquals("Aeroport Civil Badea Cartan 1tb Badea Cartan 2tb Bastion Cartier Aeroport Cartier Aeroport II DN6 DN6_II Hella Real 3 Real 3_ Tb_A.Popovici 2 UM Aeroport V.Economu 1tb V.Economu 2tb ",
			 NamedEntityCollection.sortedNames(c.getLine("E4").getStations())
		);
	}
	public void testLine_E4_StationIDs() {
		assertEquals("2968 2971 2972 3103 3104 3820 3821 3840 3860 3880 3881 5940 6240 6241 7160 ",
			 NamedEntityCollection.sortedIDs(c.getLine("E4").getStations())
		);
	}
	public void testLine_E5_StationCount() {
		assertEquals(21, c.getLine("E5").getStations().size());
	}
	public void testLine_E5_StationNames() {
		assertEquals("Ab_Brancoveanu 1 Ab_Dambovita Ab_Dambovita_ Ab_P-ta I.Maniu 1 B.A.T.M.A 2 a  B.A.T.M.A. 1 a Brancoveanu.. Cefin 1a Dermatina  Dermatina_ Metro 1a Metro 1b P-ta I.Maniu P-ta Mocioni P-ta Mocioni_ Peco 1a Peco 2a Real Practiker Real Practiker_ Veteranilor Veteranilor_ ",
			 NamedEntityCollection.sortedNames(c.getLine("E5").getStations())
		);
	}
	public void testLine_E5_StationIDs() {
		assertEquals("2666 2667 2668 2669 2673 2675 2679 2680 2682 2684 2685 2687 2690 2691 2694 2695 4760 4780 5500 5501 5520 ",
			 NamedEntityCollection.sortedIDs(c.getLine("E5").getStations())
		);
	}
	public void testLine_E6_StationCount() {
		assertEquals(8, c.getLine("E6").getStations().size());
	}
	public void testLine_E6_StationNames() {
		assertEquals("AT_Miresei_1 AT_Miresei_2 Liege Metro 2 Metro_2 P-ta C.Europei 1tb PITT Torontalului 1tb ",
			 NamedEntityCollection.sortedNames(c.getLine("E6").getStations())
		);
	}
	public void testLine_E6_StationIDs() {
		assertEquals("2957 2965 4492 4493 6000 6001 6003 6800 ",
			 NamedEntityCollection.sortedIDs(c.getLine("E6").getStations())
		);
	}
	public void testLine_E7_StationCount() {
		assertEquals(36, c.getLine("E7").getStations().size());
	}
	public void testLine_E7_StationNames() {
		assertEquals("Ab_Brancoveanu 1 Ab_P-ta I.Maniu 1 Abator p Abator s Brancoveanu.. C.I.Nottara_1 C.I.Nottara_2 Dambovita 1a Dambovita 2a F-ca de Zahar_1 F-ca de Zahar_2 Fac Electro_ Lic. Auto_ Liceul Auto Mangalia 1a Mangalia 2a P-ta L. da Vinci  P-ta Mocioni P-ta Mocioni_ Pacii 1a Pacii 2a Pod M Saguna 1a Podgoriei Podgoriei_ Polona Polona_1 Rascoala 1907_1 Rascoala 1907_2 T.Grozavescu  T.Grozavescu p Univ de Vest  Univ de Vest_ V Simionescu 1a V.Simionescu 2a Vaslui 1a Vaslui 2a ",
			 NamedEntityCollection.sortedNames(c.getLine("E7").getStations())
		);
	}
	public void testLine_E7_StationIDs() {
		assertEquals("2666 2667 2691 2694 2695 2706 2806 2807 2809 2810 2812 2813 2814 2815 2816 2819 2820 2821 2822 2826 2926 2927 2928 2929 2946 2947 2948 3741 5960 5961 5962 5963 5964 5965 6200 6260 ",
			 NamedEntityCollection.sortedIDs(c.getLine("E7").getStations())
		);
	}
	public void testLine_E7b_StationCount() {
		assertEquals(23, c.getLine("E7b").getStations().size());
	}
	public void testLine_E7b_StationNames() {
		assertEquals("Ab_Brancoveanu 1 Ab_P-ta I.Maniu 1 Brancoveanu.. Dambovita 1a Dambovita 2a Fac Electro_ Lic. Auto_ Mangalia 1a Mangalia 2a P-ta L. da Vinci  P-ta Mocioni P-ta Mocioni_ Pacii 1a Pacii 2a Pod M Saguna 1a T.Grozavescu  T.Grozavescu p Univ de Vest  Univ de Vest_ V Simionescu 1a V.Simionescu 2a Vaslui 1a Vaslui 2a ",
			 NamedEntityCollection.sortedNames(c.getLine("E7b").getStations())
		);
	}
	public void testLine_E7b_StationIDs() {
		assertEquals("2666 2667 2691 2694 2695 2706 2806 2807 2809 2810 2812 2813 2814 2815 2816 2819 2820 2821 2822 2826 3741 5965 6200 ",
			 NamedEntityCollection.sortedIDs(c.getLine("E7b").getStations())
		);
	}
	public void testLine_E8_StationCount() {
		assertEquals(28, c.getLine("E8").getStations().size());
	}
	public void testLine_E8_StationNames() {
		assertEquals("Ab_Brancoveanu_ Ab_Bujorilor_ Ab_Dambovita Ab_Dambovita_ Ab_Izlaz_ Baba Dochia  Balcescu Balcescu_ Dermatina  Fac Electro_ Fac de Mecanica_ Fac. de Mecanica Iepurelui Iepurelui_ Lic L. Calderon Lic L. Calderon_ P-ta I.Maniu P-ta L. da Vinci  Pestalozzi_ Pod C. Sagului Porumbescu_ Univ D. Cantemir_ Univ de Vest  Univ de Vest_ V. Parvan Veteranilor Veteranilor_ str. Corbului ",
			 NamedEntityCollection.sortedNames(c.getLine("E8").getStations())
		);
	}
	public void testLine_E8_StationIDs() {
		assertEquals("2668 2669 2687 2690 2806 2807 2826 2828 2829 2830 2831 2833 2836 2837 2838 2839 3741 4424 4760 4780 5320 5321 5340 5360 5361 5380 5400 6861 ",
			 NamedEntityCollection.sortedIDs(c.getLine("E8").getStations())
		);
	}
	public void testLine_M22_StationCount() {
		assertEquals(14, c.getLine("M22").getStations().size());
	}
	public void testLine_M22_StationNames() {
		assertEquals("Gh.Domasnean Mosnita Noua1 Mosnita Noua1_ Mosnita Noua2 Mosnita Noua2_ Mosnita Noua3 Mosnita Noua3_ Mosnita Noua4 Mosnita Noua4_ Mosnita Veche Moteletul Moteletul_ Otniel Otniel_ ",
			 NamedEntityCollection.sortedNames(c.getLine("M22").getStations())
		);
	}
	public void testLine_M22_StationIDs() {
		assertEquals("4427 5580 5600 5680 5681 5700 5701 5702 5720 5721 5740 5741 5760 5761 ",
			 NamedEntityCollection.sortedIDs(c.getLine("M22").getStations())
		);
	}
	public void testLine_M22a_StationCount() {
		assertEquals(21, c.getLine("M22a").getStations().size());
	}
	public void testLine_M22a_StationNames() {
		assertEquals("Bucla Urseni Gh.Domasnean Mosnita Noua1 Mosnita Noua1_ Mosnita Noua2 Mosnita Noua2_ Mosnita Noua3 Mosnita Noua3_ Mosnita Noua4 Mosnita Noua4_ Mosnita Veche Moteletul Moteletul_ Otniel Otniel_ Parc Logistic Parc Logistic_ Rampa Colonistilor Rampa Colonistilor_ Urseni Urseni_ ",
			 NamedEntityCollection.sortedNames(c.getLine("M22a").getStations())
		);
	}
	public void testLine_M22a_StationIDs() {
		assertEquals("4427 5580 5600 5601 5620 5640 5641 5642 5660 5661 5680 5681 5700 5701 5702 5720 5721 5740 5741 5760 5761 ",
			 NamedEntityCollection.sortedIDs(c.getLine("M22a").getStations())
		);
	}
	public void testLine_M22b_StationCount() {
		assertEquals(16, c.getLine("M22b").getStations().size());
	}
	public void testLine_M22b_StationNames() {
		assertEquals("Bucla Urseni Gh.Domasnean Mosnita Noua1 Mosnita Noua1_ Mosnita Noua2 Mosnita Noua2_ Moteletul Moteletul_ Otniel Otniel_ Parc Logistic Parc Logistic_ Rampa Colonistilor Rampa Colonistilor_ Urseni Urseni_ ",
			 NamedEntityCollection.sortedNames(c.getLine("M22b").getStations())
		);
	}
	public void testLine_M22b_StationIDs() {
		assertEquals("4427 5580 5601 5620 5640 5641 5642 5660 5661 5680 5720 5721 5740 5741 5760 5761 ",
			 NamedEntityCollection.sortedIDs(c.getLine("M22b").getStations())
		);
	}
	public void testLine_M24_StationCount() {
		assertEquals(11, c.getLine("M24").getStations().size());
	}
	public void testLine_M24_StationNames() {
		assertEquals("AT_Miresei_1 Dudesti_Centru La Blocuri 1 La Blocuri 2 La Roman 1 Liege_2 Metro 2 Metro_2 Miresei_2 P-ta C.Europei 1tb Torontalului 1tb ",
			 NamedEntityCollection.sortedNames(c.getLine("M24").getStations())
		);
	}
	public void testLine_M24_StationIDs() {
		assertEquals("2957 2965 4493 6003 6800 7283 7341 7343 7345 7480 7500 ",
			 NamedEntityCollection.sortedIDs(c.getLine("M24").getStations())
		);
	}
	public void testLine_M27_StationCount() {
		assertEquals(18, c.getLine("M27").getStations().size());
	}
	public void testLine_M27_StationNames() {
		assertEquals("Badea Cartan 1tb Badea Cartan 2tb Bastion Bastion s DN6 DN6_II Ianova 1 Ianova 1_ Ianova 2S Ianova 2p Remetea Mare 1 Remetea Mare 1_ Remetea Mare 2 Remetea Mare 2_ Remetea Mare 3 Remetea Mare 3_ V.Economu 1tb V.Economu 2tb ",
			 NamedEntityCollection.sortedNames(c.getLine("M27").getStations())
		);
	}
	public void testLine_M27_StationIDs() {
		assertEquals("2971 2972 3103 3104 3820 3821 5940 5980 6020 6021 6022 6023 6024 6025 6026 6027 6100 6120 ",
			 NamedEntityCollection.sortedIDs(c.getLine("M27").getStations())
		);
	}
	public void testLine_M30_StationCount() {
		assertEquals(15, c.getLine("M30").getStations().size());
	}
	public void testLine_M30_StationNames() {
		assertEquals("Badea Cartan 1tb Badea Cartan 2tb Bastion Bastion_s Ialomita Ialomita_ Lugojului Lugojului_ Pod Ghiroda Pod Ghiroda s Primaria Ghiroda Primaria Ghiroda_ Tb_A.Popovici 2 V.Economu 1tb V.Economu 2tb ",
			 NamedEntityCollection.sortedNames(c.getLine("M30").getStations())
		);
	}
	public void testLine_M30_StationIDs() {
		assertEquals("2968 2971 2972 3103 3104 5540 5780 5800 5820 5840 5841 5842 5940 6121 6160 ",
			 NamedEntityCollection.sortedIDs(c.getLine("M30").getStations())
		);
	}
	public void testLine_M35_StationCount() {
		assertEquals(14, c.getLine("M35").getStations().size());
	}
	public void testLine_M35_StationNames() {
		assertEquals("Aleea Pad.V Aleea Pad.V_ Badea Cartan 1tb Badea Cartan 2tb Bastion Bastion s G Vii Complex1 G Vii Complex1_ Giarmata Vii 1 Giarmata Vii 2p Giarmata Vii 2s Giarmata Vii1_ V.Economu 1tb V.Economu 2tb ",
			 NamedEntityCollection.sortedNames(c.getLine("M35").getStations())
		);
	}
	public void testLine_M35_StationIDs() {
		assertEquals("2971 2972 3103 3104 5940 6120 6140 6141 6142 6144 6145 6146 7200 7201 ",
			 NamedEntityCollection.sortedIDs(c.getLine("M35").getStations())
		);
	}
	public void testLine_M36_StationCount() {
		assertEquals(26, c.getLine("M36").getStations().size());
	}
	public void testLine_M36_StationNames() {
		assertEquals("Ab_P-ta I.Maniu 1 Abator p Abator s Brancoveanu.. Dambovita 1a Dambovita 2a Lic. Auto_ Liceul Auto P-ta Mocioni P-ta Mocioni_ Pod Sanm.R Pod Sanm.R_ Primarie SR Primarie SR_ Sanmh R_Peco Sanmh R_Peco_ Sanmihaiu G Sanmihaiu G_ Strand Termal Strand Termal_ Utvin Biserica Utvin Biserica_ Utvin Pl Ind Utvin Pl Ind_ Utvin balta Utvin balta_ ",
			 NamedEntityCollection.sortedNames(c.getLine("M36").getStations())
		);
	}
	public void testLine_M36_StationIDs() {
		assertEquals("2666 2667 2691 2695 2812 2821 5960 5964 5965 6260 7040 7041 7060 7061 7062 7063 7064 7065 7066 7067 7068 7080 7100 7101 7121 7140 ",
			 NamedEntityCollection.sortedIDs(c.getLine("M36").getStations())
		);
	}
	public void testLine_P1_a_StationCount() {
		assertEquals(28, c.getLine("P1-a").getStations().size());
	}
	public void testLine_P1_a_StationNames() {
		assertEquals("A Saguna  A.Lapusneanu_1 AT_Pop de Basesti_1 Ab_B.Cartan_0 Ab_Dambovita Ab_P-ta I.Maniu 3 Ab_U.M.T._0 BaltaVerde 3 C.Martirilor_2 Dep trl.iesire Domasnean_4 Drubeta_2 G.Alexandrescu 1tb Garaj Auto Holdelor 1a Liege 3tb Petru si Pavel 2tb Remus Radulet 1tb Renasterii 2tb. Stuparilor 1a Torontalului 1tb Tv_Cetatii 1 Tv_Circumv 1 Tv_Electrica 2 Tv_Gara  Est 2 Tv_Lalelelor 1 Tv_Sarmiseg 1 V.Economu 2tb ",
			 NamedEntityCollection.sortedNames(c.getLine("P1-a").getStations())
		);
	}
	public void testLine_P1_a_StationIDs() {
		assertEquals("2669 2745 2748 2765 2767 2824 2889 2957 2960 2961 2963 2972 3019 3247 3248 3411 3546 3552 3581 3587 3591 3642 3701 4022 4300 4322 4464 4800 ",
			 NamedEntityCollection.sortedIDs(c.getLine("P1-a").getStations())
		);
	}
	public void testLine_P1_d_StationCount() {
		assertEquals(30, c.getLine("P1-d").getStations().size());
	}
	public void testLine_P1_d_StationNames() {
		assertEquals("AT_Miresei_2 AT_Pop de Basesti_2 Ab_P-ta I.Maniu 3 Ab_U.M.T_1 Baba Dochia  Badea Cartan 1tb Bv Sudului_1 Bv. Cetatii 1tb C.Martirilor_1 Dep trl.iesire Domasnean_3 Drubeta_1 G.Alexandrescu 1tb G.Alexandrescu_2 Garaj Auto L.Rebreanu_1 Liege 2tb. Petru si Pavel 1a Pomiculturii 1tb Renasterii 1tb. Stuparilor 2a Tb_Gara  Nord 1 Tv_Amforei 2 Tv_BaltaVerde 2 Tv_Circumv 1 Tv_Economu 1 Tv_Electrica 1 Tv_Gara  Est 1 Tv_Lalelelor 2 Tv_Sarmiseg 2 ",
			 NamedEntityCollection.sortedNames(c.getLine("P1-d").getStations())
		);
	}
	public void testLine_P1_d_StationIDs() {
		assertEquals("2747 2749 2763 2766 2768 2823 2889 2951 3013 3019 3103 3176 3250 3281 3545 3549 3552 3580 3582 3586 3590 3596 3645 3701 4022 4323 4424 4428 4465 4492 ",
			 NamedEntityCollection.sortedIDs(c.getLine("P1-d").getStations())
		);
	}
	public void testLine_P2_a_StationCount() {
		assertEquals(18, c.getLine("P2-a").getStations().size());
	}
	public void testLine_P2_a_StationNames() {
		assertEquals("Ab_P-ta I.Maniu 3 Ab_U.M.T_1 Baba Dochia  Badea Cartan 1tb C.Martirilor_1 Dep trl.iesire Drubeta_1 G.Alexandrescu 1tb Garaj Auto L.Rebreanu_1 Liege 2tb. Petru si Pavel 1a Stuparilor 2a Tv_BaltaVerde 2 Tv_Circumv 1 Tv_Domasnean 1 Tv_Economu 1 Tv_Torontal 1 ",
			 NamedEntityCollection.sortedNames(c.getLine("P2-a").getStations())
		);
	}
	public void testLine_P2_a_StationIDs() {
		assertEquals("2889 2951 3019 3103 3176 3250 3540 3549 3552 3566 3586 3590 3596 3701 4022 4323 4424 4428 ",
			 NamedEntityCollection.sortedIDs(c.getLine("P2-a").getStations())
		);
	}
	public void testLine_P2_d_StationCount() {
		assertEquals(27, c.getLine("P2-d").getStations().size());
	}
	public void testLine_P2_d_StationNames() {
		assertEquals("A.Saguna 1a Ab_Gara de Est_0 Ab_P-ta I.Maniu 3 Badea Cartan 2tb BaltaVerde 3 Brancoveanu_2 Bv. Cetatii 1tb C.Martirilor_2 Dep trl.iesire Domasnean_4 G.Alexandrescu 1tb Gara de Nord 2tb Garaj Auto Holdelor 1a L.Rebreanu_2 Liege 3tb Petru si Pavel 2tb Remus Radulet 1tb Renasterii 2tb. Stuparilor 1a Tv_Circumv 1 Tv_Electrica 2 Tv_Lalelelor 1 Tv_Sarmiseg 1 Tv_Torontal 1 U.M.T._2 plecare V.Economu 2tb ",
			 NamedEntityCollection.sortedNames(c.getLine("P2-d").getStations())
		);
	}
	public void testLine_P2_d_StationIDs() {
		assertEquals("2745 2748 2765 2824 2827 2889 2960 2961 2963 2972 3013 3019 3104 3106 3247 3248 3340 3540 3552 3581 3587 3593 3597 3701 4022 4320 4800 ",
			 NamedEntityCollection.sortedIDs(c.getLine("P2-d").getStations())
		);
	}
	public void testLine_P3_StationCount() {
		assertEquals(26, c.getLine("P3").getStations().size());
	}
	public void testLine_P3_StationNames() {
		assertEquals("Ab_P-ta I.Maniu 1 Abator p Abator s Balcescu Balcescu_ Brancoveanu.. Brancoveanu_ C.I.Nottara_2 Dambovita 1a Dambovita 2a F-ca de Zahar_1 F-ca de Zahar_2 Garaj Auto Lic. Auto_ Liceul Auto Mangalia 1a Mangalia 2a Pacii 1a Pacii 2a Podgoriei_ Polona Polona_1 Porumbescu Rascoala 1907_1 Rascoala 1907_2 V.Simionescu 2a ",
			 NamedEntityCollection.sortedNames(c.getLine("P3").getStations())
		);
	}
	public void testLine_P3_StationIDs() {
		assertEquals("2667 2691 2701 2740 2810 2812 2815 2819 2820 2821 2822 2926 2928 2929 2946 2947 2948 4022 5321 5380 5960 5961 5963 5964 5965 6260 ",
			 NamedEntityCollection.sortedIDs(c.getLine("P3").getStations())
		);
	}
	public void testLine_P4_a_StationCount() {
		assertEquals(15, c.getLine("P4-a").getStations().size());
	}
	public void testLine_P4_a_StationNames() {
		assertEquals("Ab_Izlaz_ Ab_P-ta I.Maniu 3 Ab_Pod C.Sagului 1 Brancoveanu_2 Campului Dep trl.iesire Domasnean_3 Garaj Auto Gh.Domasnean_ L.Rebreanu_2 M.C-tin Prezan 1tb. Scoala Plopi 1a Tv-Mures Tv_Ciarda R 2 Victor Hugo_ ",
			 NamedEntityCollection.sortedNames(c.getLine("P4-a").getStations())
		);
	}
	public void testLine_P4_a_StationIDs() {
		assertEquals("2728 2836 2844 2889 3420 3574 3580 3593 3597 3701 4022 4503 5000 5181 5920 ",
			 NamedEntityCollection.sortedIDs(c.getLine("P4-a").getStations())
		);
	}
	public void testLine_P4_d_StationCount() {
		assertEquals(15, c.getLine("P4-d").getStations().size());
	}
	public void testLine_P4_d_StationNames() {
		assertEquals("Banatul_1 Bujorilor Bv.Sudului(bucla) 2tb C Popescu 1a Campului Dep trl.iesire Garaj Auto Gh.Domasnean L.Rebreanu_1 L.Rebreanu_2 Lidia Lidia_ P-ta I.Maniu Pod C. Sagului Tv_Ciarda R 1 ",
			 NamedEntityCollection.sortedNames(c.getLine("P4-d").getStations())
		);
	}
	public void testLine_P4_d_StationIDs() {
		assertEquals("2668 2729 2844 3006 3408 3573 3596 3600 3701 4022 4427 4980 4981 5340 7540 ",
			 NamedEntityCollection.sortedIDs(c.getLine("P4-d").getStations())
		);
	}
	public void testLine_Tb11_StationCount() {
		assertEquals(29, c.getLine("Tb11").getStations().size());
	}
	public void testLine_Tb11_StationNames() {
		assertEquals("Arena Aqua 1tb Arena Aqua 2tb Badea Cartan 1tb Badea Cartan 2tb Gara de Nord 2tb ILSA 1tb. Jiul 1tb. Jiul 2tb. P-ta 700 1tb. P-ta 700 2tb Pop de Basesti 1tb Pop de Basesti 2tb. Regina Maria 1tb Regina Maria 2tb Renasterii 1tb. Renasterii 2tb. Samuil Micu 1tb Samuil Micu 2tb Tb_A.Popovici 2 Tb_Bastion 1 Tb_Gara  Nord 1 Tb_Ghe.Baritiu 1 Tb_Ghe.Baritiu 2 Tb_Oituz 2 Tb_P-ta Marasti 1 Tb_Pol.Jud.Timis 1 Tb_Pol.Jud.Timis 2. V.Economu 1tb V.Economu 2tb ",
			 NamedEntityCollection.sortedNames(c.getLine("Tb11").getStations())
		);
	}
	public void testLine_Tb11_StationIDs() {
		assertEquals("2671 2672 2764 2768 2772 2773 2781 2798 2800 2808 2823 2824 2967 2968 2971 2972 2973 2974 3060 3102 3103 3104 3105 3106 3120 3160 3161 3720 4000 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tb11").getStations())
		);
	}
	public void testLine_Tb14_StationCount() {
		assertEquals(25, c.getLine("Tb14").getStations().size());
	}
	public void testLine_Tb14_StationNames() {
		assertEquals("Cim. Eroilor 1tb Cim.Eroilor 2tb Div 9 Cavalerie 2a Div.9 Cavalerie 1tb Gara de Nord 2tb I.I.de la Brad 1tb. I.I.de la Brad 2tb. Iulius Mall 1tb. Jiul 1tb. Jiul 2tb. P-ta 700 1tb. P-ta 700 2tb P-ta C.Europei 2tb Petru si Pavel 1tb Petru si Pavel 2tb Pomiculturii 1a Pomiculturii 1tb Pop de Basesti 1tb Pop de Basesti 2tb. Regina Maria 1tb Regina Maria 2tb Tb_Gara  Nord 1 Tb_Ghe.Baritiu 1 Tb_Ghe.Baritiu 2 Tb_P-ta Marasti 1 ",
			 NamedEntityCollection.sortedNames(c.getLine("Tb14").getStations())
		);
	}
	public void testLine_Tb14_StationIDs() {
		assertEquals("2671 2672 2764 2768 2772 2773 2781 2784 2793 2794 2962 2963 2964 2966 3060 3102 3105 3106 3120 3255 3280 3281 3361 3362 3720 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tb14").getStations())
		);
	}
	public void testLine_Tb15_StationCount() {
		assertEquals(13, c.getLine("Tb15").getStations().size());
	}
	public void testLine_Tb15_StationNames() {
		assertEquals("Cluj 1tb. Cluj 2tb. Comp. Studentesc 1tb Comp. Studentesc 2tb M.C-tin Prezan 1tb. M.C-tin Prezan 2tb. Mart.I.Stanciu 1tb. Mart.I.Stanciu 2tb. Martirilor 1tb. Martirilor 2tb. Prezan (bucla) T.Grozavescu  T.Grozavescu p ",
			 NamedEntityCollection.sortedNames(c.getLine("Tb15").getStations())
		);
	}
	public void testLine_Tb15_StationIDs() {
		assertEquals("2706 2716 2718 2720 2722 2724 2726 2728 2732 2979 2980 3041 6200 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tb15").getStations())
		);
	}
	public void testLine_Tb16_StationCount() {
		assertEquals(13, c.getLine("Tb16").getStations().size());
	}
	public void testLine_Tb16_StationNames() {
		assertEquals("Bv. Sudului 1tb Bv.Sudului(bucla) Bv.Sudului(bucla) 2tb Cluj 1tb. Cluj 2tb. Comp. Studentesc 1tb Comp. Studentesc 2tb Cv.Sudului 1tb Cv.Sudului 2tb Sp.Judetean Sp.Judetean 2tb T.Grozavescu  T.Grozavescu p ",
			 NamedEntityCollection.sortedNames(c.getLine("Tb16").getStations())
		);
	}
	public void testLine_Tb16_StationIDs() {
		assertEquals("2706 2716 2718 2979 2980 3000 3002 3003 3004 3005 3006 4860 6200 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tb16").getStations())
		);
	}
	public void testLine_Tb17_StationCount() {
		assertEquals(17, c.getLine("Tb17").getStations().size());
	}
	public void testLine_Tb17_StationNames() {
		assertEquals("Agronomie 1tb Agronomie 2tb Dacia Service 2tb Liege 1tb Liege 2tb. P-ta C.Europei 1tb P-ta C.Europei 2tb Remus Radulet 1tb Tb_A.Popovici 2 Tb_Baader 1 Tb_Bastion 1 Tb_Oituz 2 Tb_P-ta Marasti 2 Tb_Pol.Jud.Timis 1 Tb_Pol.Jud.Timis 2. U.T.T. 1tb U.T.T. 2tb ",
			 NamedEntityCollection.sortedNames(c.getLine("Tb17").getStations())
		);
	}
	public void testLine_Tb17_StationIDs() {
		assertEquals("2798 2800 2950 2951 2952 2953 2954 2955 2956 2961 2965 2966 2967 2968 3080 4000 4100 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tb17").getStations())
		);
	}
	public void testLine_Tb18_StationCount() {
		assertEquals(21, c.getLine("Tb18").getStations().size());
	}
	public void testLine_Tb18_StationNames() {
		assertEquals("AT_Miresei_1 C.P.Solventul 1tb. C.P.Solventul 2tb Gara de Nord 2tb Gelu 1tb. Gelu 2tb. Jiul 1tb. Jiul 2tb. Liege 3tb P-ta 700 1tb. P-ta 700 2tb P-ta C.Europei 1tb P-ta C.Europei 2tb Pop de Basesti 1tb Pop de Basesti 2tb. Regina Maria 1tb Regina Maria 2tb Remus Radulet 1tb Tb_Gara  Nord 1 Tb_P-ta Marasti 1 Torontalului 1tb ",
			 NamedEntityCollection.sortedNames(c.getLine("Tb18").getStations())
		);
	}
	public void testLine_Tb18_StationIDs() {
		assertEquals("2756 2759 2760 2764 2768 2772 2773 2781 2957 2960 2961 2965 2966 3060 3102 3105 3106 3107 3120 3720 4493 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tb18").getStations())
		);
	}
	public void testLine_Tb19_StationCount() {
		assertEquals(15, c.getLine("Tb19").getStations().size());
	}
	public void testLine_Tb19_StationNames() {
		assertEquals("A.Saguna 1tb Cluj 1tb. Cluj 2tb. Comp. Studentesc 1tb Comp. Studentesc 2tb I.C.Bratianu  M.C-tin Prezan 1tb. M.C-tin Prezan 2tb. M.Eminescu 1tb Mart.I.Stanciu 1tb. Mart.I.Stanciu 2tb. Martirilor 1tb. Martirilor 2tb. Prezan (bucla) Univ de Vest  ",
			 NamedEntityCollection.sortedNames(c.getLine("Tb19").getStations())
		);
	}
	public void testLine_Tb19_StationIDs() {
		assertEquals("2716 2718 2720 2722 2724 2726 2728 2732 2807 2975 2976 2977 2979 2980 3041 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tb19").getStations())
		);
	}
	public void testLine_Tv1_StationCount() {
		assertEquals(33, c.getLine("Tv1").getStations().size());
	}
	public void testLine_Tv1_StationNames() {
		assertEquals("Tv_3 August 1 Tv_3 August 2 Tv_B.Cartan 1 Tv_Catedrala 1 Tv_Catedrala 2 Tv_Continental 1 Tv_Continental 2 Tv_Economu 1 Tv_Electrica 1 Tv_Gara  Est 1 Tv_Gara  Nord 1 Tv_Gara  Nord 2 Tv_Lalelelor 1 Tv_Libertatii 1 Tv_Libertatii 2 Tv_Meteo 1 Tv_Mocioni 1 Tv_Mocioni 2 Tv_P Turcesc 1 Tv_P-ta 700 1 Tv_P-ta 700 2 Tv_P-ta Maria 2 Tv_P-ta Maria 3 Tv_P-ta Traian 1 Tv_P-ta Traian 2 Tv_Prefectura 1 Tv_Prefectura 2 Tv_R Carol 1 Tv_R Carol 2 Tv_Sarmiseg 1 Tv_U.M.T - gara Tv_U.M.T 1 Tv_V.Babes 1 ",
			 NamedEntityCollection.sortedNames(c.getLine("Tv1").getStations())
		);
	}
	public void testLine_Tv1_StationIDs() {
		assertEquals("2702 2704 2708 2710 2727 2735 2745 2748 2752 2754 2757 2763 2766 3163 3164 3165 3166 3167 3168 3169 3170 3171 3172 3173 3174 3175 3176 3181 3243 3244 3249 3253 4640 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tv1").getStations())
		);
	}
	public void testLine_Tv2_StationCount() {
		assertEquals(39, c.getLine("Tv2").getStations().size());
	}
	public void testLine_Tv2_StationNames() {
		assertEquals("Ab_U.M.T._0 Regele Carol_3 Tv_3 August 1 Tv_3 August 2 Tv_B.Cartan 2 Tv_Catedrala 1 Tv_Catedrala 2 Tv_Continental 1 Tv_Continental 2 Tv_Crizantem 2 Tv_Crizantem_1 Tv_Dambovita 1 Tv_Dambovita 2 Tv_Economu 2 Tv_Electrica 2 Tv_Gara  Est 2 Tv_I Maniu 1 Tv_I Maniu 2 Tv_Lalelelor 2 Tv_Libertatii 1 Tv_Libertatii 2 Tv_Mangalia 1 Tv_Mangalia 2 Tv_Meteo 2 Tv_Mocioni 1 Tv_Mocioni 2 Tv_P Turcesc 2 Tv_P-ta 700 1 Tv_P-ta 700 2 Tv_P-ta Maria 2 Tv_P-ta Maria 3 Tv_P-ta Traian 1 Tv_P-ta Traian 2 Tv_Prefectura 1 Tv_Prefectura 2 Tv_R Carol 2 Tv_Sarmiseg 2 Tv_V.Babes 2 U.M.T_2 ",
			 NamedEntityCollection.sortedNames(c.getLine("Tv2").getStations())
		);
	}
	public void testLine_Tv2_StationIDs() {
		assertEquals("2704 2708 2710 2727 2738 2747 2749 2753 2755 2761 2765 2767 2880 2881 2882 2883 2884 2885 2886 2887 2888 3163 3164 3165 3166 3167 3168 3169 3170 3171 3172 3173 3174 3175 3177 3181 3254 4322 4640 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tv2").getStations())
		);
	}
	public void testLine_Tv3b_StationCount() {
		assertEquals(12, c.getLine("Tv3b").getStations().size());
	}
	public void testLine_Tv3b_StationNames() {
		assertEquals("Regele Carol_3 Tv_Crizantem 2 Tv_Crizantem_1 Tv_Dambovita 1 Tv_Dambovita 2 Tv_Gara  Nord 1 Tv_Gara  Nord 2 Tv_I Maniu 1 Tv_I Maniu 2 Tv_Mangalia 1 Tv_Mangalia 2 Tv_R Carol 1 ",
			 NamedEntityCollection.sortedNames(c.getLine("Tv3b").getStations())
		);
	}
	public void testLine_Tv3b_StationIDs() {
		assertEquals("2702 2880 2881 2882 2883 2884 2885 2886 2887 2888 3243 3244 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tv3b").getStations())
		);
	}
	public void testLine_Tv4_StationCount() {
		assertEquals(44, c.getLine("Tv4").getStations().size());
	}
	public void testLine_Tv4_StationNames() {
		assertEquals("Tv_3 August 1 Tv_3 August 2 Tv_Amforei 1 Tv_Amforei 2 Tv_BaltaVerde 1 Tv_BaltaVerde 2 Tv_Banatim 1 Tv_Banatim 2 Tv_Cetatii  2 Tv_Cetatii 1 Tv_Ciarda R 1 Tv_Ciarda R 2 Tv_Circumv 1 Tv_Circumv 2 Tv_Continental 1 Tv_Continental 2 Tv_Detergenti 1 Tv_Detergenti 2 Tv_Domasnean 1 Tv_Domasnean 2 Tv_Electrotim 1 Tv_Electrotim 2 Tv_F de Bere 1 Tv_F de Bere 2 Tv_IOT 1 Tv_IOT 2 Tv_Libertatii 1 Tv_Libertatii 2 Tv_M.Basarab 1 Tv_M.Basarab 2 Tv_Mecatim 1 Tv_Mecatim 2 Tv_Mendeleev 1 Tv_Mendeleev 2 Tv_P-ta 700 1 Tv_P-ta 700 2 Tv_P-ta Traian 1 Tv_P-ta Traian 2 Tv_Prefectura 1 Tv_Prefectura 2 Tv_Spumotim 1 Tv_Spumotim 2 Tv_Torontal 1 Tv_Torontal 2 ",
			 NamedEntityCollection.sortedNames(c.getLine("Tv4").getStations())
		);
	}
	public void testLine_Tv4_StationIDs() {
		assertEquals("3164 3165 3166 3167 3168 3169 3170 3171 3172 3173 3174 3175 3540 3542 3543 3544 3545 3546 3547 3548 3549 3550 3551 3552 3553 3554 3556 3560 3561 3563 3564 3565 3566 3567 3568 3569 3571 3572 3573 3574 4680 4700 4701 4720 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tv4").getStations())
		);
	}
	public void testLine_Tv5_StationCount() {
		assertEquals(40, c.getLine("Tv5").getStations().size());
	}
	public void testLine_Tv5_StationNames() {
		assertEquals("A.Guttenbrun_1 A.Guttenbrun_2 A.Lapusneanu_1 A.Lapusneanu_2 Balcescu_2 BaltaVerde 3 BaltaVerde 4 Banatim_3 Brediceanu_1 Deliblata_2 G.Alexandrescu_1 G.Alexandrescu_2 Macilor_1 Macilor_2 Madona_1 Madona_2 Olimpia_2 P-ta Crucii_2 P-ta Traian p Parc Doina_2 Razboieni_1 Razboieni_2 Ronat_1 Ronat_2 Str.Cluj_2 Tv_3 August 1 Tv_Catedrala 1 Tv_Circumv 1 Tv_Circumv 2 Tv_Continental 1 Tv_F de Bere 1 Tv_Libertatii 1 Tv_Mendeleev 1 Tv_Mendeleev 2 Tv_P-ta 700 1 Tv_P-ta Maria 1 Tv_P-ta Traian 1 Tv_Prefectura 1 Zalau_1 Zalau_2 ",
			 NamedEntityCollection.sortedNames(c.getLine("Tv5").getStations())
		);
	}
	public void testLine_Tv5_StationIDs() {
		assertEquals("3164 3166 3168 3170 3172 3174 3550 3551 3552 3553 3554 3640 3641 3642 3643 3644 3645 3646 3647 3648 3649 3650 3651 3652 3653 3661 3663 3680 3682 3684 3686 4041 4640 4800 4801 4920 6840 6880 7220 7221 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tv5").getStations())
		);
	}
	public void testLine_Tv6_StationCount() {
		assertEquals(32, c.getLine("Tv6").getStations().size());
	}
	public void testLine_Tv6_StationNames() {
		assertEquals("Balcescu_1 Banatim_4 Brediceanu_2 Deliblata_1 Olimpia_1 P-ta Crucii_1 Parc Doina_1 Str.Cluj_1 Tv_3 August 2 Tv_Amforei 1 Tv_Amforei 2 Tv_BaltaVerde 1 Tv_BaltaVerde 2 Tv_Catedrala 2 Tv_Cetatii  2 Tv_Cetatii 1 Tv_Circumv 1 Tv_Circumv 2 Tv_Continental 2 Tv_F de Bere 2 Tv_Libertatii 2 Tv_M.Basarab 1 Tv_M.Basarab 2 Tv_Mendeleev 1 Tv_Mendeleev 2 Tv_P-ta 700 2 Tv_P-ta Maria 2 Tv_P-ta Traian 2 Tv_P-ta Traian pl Tv_Prefectura 2 Tv_Torontal 1 Tv_Torontal 2 ",
			 NamedEntityCollection.sortedNames(c.getLine("Tv6").getStations())
		);
	}
	public void testLine_Tv6_StationIDs() {
		assertEquals("3163 3165 3167 3169 3171 3173 3175 3181 3540 3542 3543 3544 3545 3546 3547 3548 3549 3550 3551 3552 3553 3556 3660 3662 3664 3681 3683 3685 4042 4680 6841 6900 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tv6").getStations())
		);
	}
	public void testLine_Tv7a_StationCount() {
		assertEquals(21, c.getLine("Tv7a").getStations().size());
	}
	public void testLine_Tv7a_StationNames() {
		assertEquals("Balcescu_1 Banatul_1 Chisodei Drubeta_4 E.Zola Izlaz Lidia Memorandului Parc Doina_1 Pavlov Progresul Regele Carol_3 Transilvania_3 Tv-Mures Tv_Crizantem_1 Tv_Dambovita 1 Tv_I Maniu 1 Tv_Mangalia 1 Tv_Mocioni 1 Tv_P-ta Maria 3 Veteranilor. ",
			 NamedEntityCollection.sortedNames(c.getLine("Tv7a").getStations())
		);
	}
	public void testLine_Tv7a_StationIDs() {
		assertEquals("2708 2727 2734 2880 2882 2884 2886 2888 3600 3660 3662 4980 5000 5020 5040 5060 5080 5100 5120 5140 5160 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tv7a").getStations())
		);
	}
	public void testLine_Tv7b_StationCount() {
		assertEquals(21, c.getLine("Tv7b").getStations().size());
	}
	public void testLine_Tv7b_StationNames() {
		assertEquals("Balcescu_2 Banatul_2 Chisodei_ Drubeta _3 E.Zola_ Izlaz_ Lidia_ Memorandului_ Parc Doina_2 Pavlov_ Progresul_ Transilvania_2 Tv-Mures_ Tv_Crizantem 2 Tv_Dambovita 2 Tv_I Maniu 2 Tv_Mangalia 2 Tv_Mocioni 2 Tv_P-ta Maria 1 Tv_R Carol 2 Veteranilor.. ",
			 NamedEntityCollection.sortedNames(c.getLine("Tv7b").getStations())
		);
	}
	public void testLine_Tv7b_StationIDs() {
		assertEquals("2704 2710 2713 2881 2883 2885 2887 3599 3601 3661 3663 4431 4920 4981 5001 5021 5041 5061 5081 5101 5141 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tv7b").getStations())
		);
	}
	public void testLine_Tv8_StationCount() {
		assertEquals(30, c.getLine("Tv8").getStations().size());
	}
	public void testLine_Tv8_StationNames() {
		assertEquals("Balcescu_1 Balcescu_2 Banatim_4 Deliblata_1 Deliblata_2 Olimpia_1 Olimpia_2 P-ta Crucii_1 P-ta Crucii_2 Parc Doina_1 Parc Doina_2 Str.Cluj_1 Str.Cluj_2 Tv_Banatim 2 Tv_Detergenti 1 Tv_Detergenti 2 Tv_Domasnean 1 Tv_Domasnean 2 Tv_Gara  Nord 1 Tv_Gara  Nord 2 Tv_Mecatim 1 Tv_Mecatim 2 Tv_Mocioni 1 Tv_Mocioni 2 Tv_P-ta Maria 1 Tv_P-ta Maria 3 Tv_R Carol 1 Tv_R Carol 2 Tv_Spumotim 1 Tv_Spumotim 2 ",
			 NamedEntityCollection.sortedNames(c.getLine("Tv8").getStations())
		);
	}
	public void testLine_Tv8_StationIDs() {
		assertEquals("2702 2704 2708 2710 2727 3243 3244 3560 3563 3564 3565 3566 3567 3660 3661 3662 3663 3664 3680 3681 3682 3683 3684 3685 3686 4042 4700 4701 4720 4920 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tv8").getStations())
		);
	}
	public void testLine_Tv9_StationCount() {
		assertEquals(24, c.getLine("Tv9").getStations().size());
	}
	public void testLine_Tv9_StationNames() {
		assertEquals("Banatul_1 Banatul_2 Brancoveanu Brancoveanu_2 Bv Sudului_1 Bv Sudului_2 C.Martirilor_1 C.Martirilor_2 Dambovita_3 Dambovita_4 Domasnean_3 Domasnean_4 Drubeta_1 Drubeta_2 Gh.Ranetti_1 Gh.Ranetti_2 L.Rebreanu_1 L.Rebreanu_2 Salcamilor_1 Salcamilor_2 Sp Judetean_1 Sp Judetean_2 Transilvania_1 Transilvania_2 ",
			 NamedEntityCollection.sortedNames(c.getLine("Tv9").getStations())
		);
	}
	public void testLine_Tv9_StationIDs() {
		assertEquals("3580 3581 3582 3583 3584 3585 3586 3587 3588 3589 3590 3591 3592 3593 3594 3595 3596 3597 3598 3599 3600 3601 3602 3620 ",
			 NamedEntityCollection.sortedIDs(c.getLine("Tv9").getStations())
		);
	}

}
