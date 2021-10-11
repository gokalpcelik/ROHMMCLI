#!/usr/bin/env Rscript
# Example inputfile format is below. Tab seperated. 

#chr	Start	End	Ref	Alt	MAF	Dist
#1	11008	11008	C	G	0.06565656565656566	392
#1	11012	11012	C	G	0.06565656565656566	4
#1	13110	13110	G	A	0.0707070707070707	2098
#1	13116	13116	T	G	0.16161616161616163	6
#1	13118	13118	A	G	0.16161616161616163	2
#1	13273	13273	G	C	0.16666666666666666	155
#1	13494	13494	A	G	0.005050505050505051	221
#1	14464	14464	A	T	0.19696969696969696	970
#1	14599	14599	T	A	0.1717171717171717	135
#1	14604	14604	A	G	0.1717171717171717	5
#1	14930	14930	A	G	0.51010101010101	326
#1	14933	14933	G	A	0.045454545454545456	3
#1	15211	15211	T	G	0.702020202020202	278
#1	15644	15644	G	A	0.005050505050505051	433
#1	15774	15774	G	A	0.005050505050505051	130
#1	15820	15820	G	T	0.2727272727272727	46
#1	16949	16949	A	C	0.020202020202020204	1046
#....
#..


args <- commandArgs(trailingOnly = TRUE)

inputfile <- as.character(args[1])

outfile <- as.character(args[2])

library(HMM)
library(readr)

#Ignore the static emission and transition parameters. generateEmmissionMatrix will replace emission parameters and transition probs will be calculated within the loop based on coefficients.

hmmForSim <- initHMM(States = c("ROH","NORM"), Symbols = c("1","2","3"), startProbs = c(0.1,0.9), transProbs = matrix(c(0.5,0.5,0.5,0.5),2), emissionProbs = matrix(c(0.989,0.989,0.001,0.010,0.010,0.001),2))

siminput <- read_delim(inputfile, "\t", escape_double = FALSE, trim_ws = TRUE)

runSimulation <- function(hmm, variantinfo, Rcoef, Ncoef, autozygosity, outputFileName)
{
  
  library(readr)
  library(HMM)
  len <- length(variantinfo[["chr"]])
  simulation <- simROH(hmm, len, variantinfo[["Dist"]], variantinfo[["MAF"]], Rcoef, Ncoef, autozygosity)
  write_delim(cbind(variantinfo, simulation), outputFileName, delim = "\t")
  
}

generateEmissionMatrix <- function(MAF)
{
  p <- 1-MAF
  q <- MAF
  ROH <- c(p,0,q)
  NORM <- c(p*p, 2*p*q, q*q)
  return (cbind(ROH, NORM))
}



simROH <- function (hmm, length, distancemap, MAF, Rcoef, Ncoef, autozygosity)
{
  hmm$transProbs[is.na(hmm$transProbs)] = 0
  states = vector("character", length)
  emission = vector("character", length)
  states[1] = sample(hmm$States, 1, prob = hmm$startProbs)
  
  ROHs <- hmm$transProbs["ROH", "NORM"]
  
  NORMs <- hmm$transProbs["NORM", "ROH"]
  
  ROHcount <- 0
  
  ROHcoef <- Rcoef
  NORMcoef <- Ncoef
  
  for (i in 2:length) {
    if (states[i - 1] == hmm$States[1] && (ROHcount/length) < autozygosity)
    {
      
      ROHcount <- ROHcount + 1
      state = sample(hmm$States, 1, prob = c(1 - ROHs * (1 - exp(
        -1 * distancemap[i] / ROHcoef
      )), ROHs * (1 - exp(
        -1 * distancemap[i] / ROHcoef
      ))))
      states[i] = state
    }
    else if (states[i - 1] == hmm$States[2] && (ROHcount/length) < autozygosity)
    {
      state = sample(hmm$States, 1, prob = c(NORMs * (1 - exp(
        -1 * distancemap[i] / NORMcoef
      )), 1 - NORMs * (1 - exp(
        -1 * distancemap[i] / NORMcoef
      ))))
      states[i] = state
    }
    else
      states[i] = hmm$States[2]
    
    
  }
  for (i in 1:length) {
    
    emmatrix <- generateEmissionMatrix(MAF[i])
    
    if(states[i] == "ROH")
    {
      emi = sample(hmm$Symbols, 1, prob = emmatrix[, "ROH"])
      emission[i] = emi
    }
    else
    {
      emi = sample(hmm$Symbols, 1, prob = emmatrix[,"NORM"])
      emission[i] = emi
    }
  }
  return(list(states = states, observation = emission))
}


#sample2 thru 7 will generate samples with different roh lengths. 

runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 100000, Ncoef = 2500000 , autozygosity = 0.02, outputFileName = paste(outfile, "_sample2_az02.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 100000, Ncoef = 2500000 , autozygosity = 0.03, outputFileName = paste(outfile, "_sample2_az03.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 100000, Ncoef = 2500000 , autozygosity = 0.04, outputFileName = paste(outfile, "_sample2_az04.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 100000, Ncoef = 2500000 , autozygosity = 0.05, outputFileName = paste(outfile, "_sample2_az05.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 100000, Ncoef = 2500000 , autozygosity = 0.06, outputFileName = paste(outfile, "_sample2_az06.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 100000, Ncoef = 2500000 , autozygosity = 0.07, outputFileName = paste(outfile, "_sample2_az07.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 100000, Ncoef = 2500000 , autozygosity = 0.08, outputFileName = paste(outfile, "_sample2_az08.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 100000, Ncoef = 2500000 , autozygosity = 0.09, outputFileName = paste(outfile, "_sample2_az09.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 100000, Ncoef = 2500000 , autozygosity = 0.10, outputFileName = paste(outfile, "_sample2_az10.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 100000, Ncoef = 2500000 , autozygosity = 0.11, outputFileName = paste(outfile, "_sample2_az11.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 100000, Ncoef = 2500000 , autozygosity = 0.12, outputFileName = paste(outfile, "_sample2_az12.txt", sep = ""))

runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 340000, Ncoef = 2260000 , autozygosity = 0.02, outputFileName = paste(outfile, "_sample3_az02.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 340000, Ncoef = 2260000 , autozygosity = 0.03, outputFileName = paste(outfile, "_sample3_az03.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 340000, Ncoef = 2260000 , autozygosity = 0.04, outputFileName = paste(outfile, "_sample3_az04.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 340000, Ncoef = 2260000 , autozygosity = 0.05, outputFileName = paste(outfile, "_sample3_az05.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 340000, Ncoef = 2260000 , autozygosity = 0.06, outputFileName = paste(outfile, "_sample3_az06.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 340000, Ncoef = 2260000 , autozygosity = 0.07, outputFileName = paste(outfile, "_sample3_az07.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 340000, Ncoef = 2260000 , autozygosity = 0.08, outputFileName = paste(outfile, "_sample3_az08.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 340000, Ncoef = 2260000 , autozygosity = 0.09, outputFileName = paste(outfile, "_sample3_az09.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 340000, Ncoef = 2260000 , autozygosity = 0.10, outputFileName = paste(outfile, "_sample3_az10.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 340000, Ncoef = 2260000 , autozygosity = 0.11, outputFileName = paste(outfile, "_sample3_az11.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 340000, Ncoef = 2260000 , autozygosity = 0.12, outputFileName = paste(outfile, "_sample3_az12.txt", sep = ""))

runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 580000, Ncoef = 2020000 , autozygosity = 0.02, outputFileName = paste(outfile, "_sample4_az02.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 580000, Ncoef = 2020000 , autozygosity = 0.03, outputFileName = paste(outfile, "_sample4_az03.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 580000, Ncoef = 2020000 , autozygosity = 0.04, outputFileName = paste(outfile, "_sample4_az04.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 580000, Ncoef = 2020000 , autozygosity = 0.05, outputFileName = paste(outfile, "_sample4_az05.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 580000, Ncoef = 2020000 , autozygosity = 0.06, outputFileName = paste(outfile, "_sample4_az06.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 580000, Ncoef = 2020000 , autozygosity = 0.07, outputFileName = paste(outfile, "_sample4_az07.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 580000, Ncoef = 2020000 , autozygosity = 0.08, outputFileName = paste(outfile, "_sample4_az08.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 580000, Ncoef = 2020000 , autozygosity = 0.09, outputFileName = paste(outfile, "_sample4_az09.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 580000, Ncoef = 2020000 , autozygosity = 0.10, outputFileName = paste(outfile, "_sample4_az10.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 580000, Ncoef = 2020000 , autozygosity = 0.11, outputFileName = paste(outfile, "_sample4_az11.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 580000, Ncoef = 2020000 , autozygosity = 0.12, outputFileName = paste(outfile, "_sample4_az12.txt", sep = ""))

runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 820000, Ncoef = 1780000 , autozygosity = 0.02, outputFileName = paste(outfile, "_sample5_az02.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 820000, Ncoef = 1780000 , autozygosity = 0.03, outputFileName = paste(outfile, "_sample5_az03.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 820000, Ncoef = 1780000 , autozygosity = 0.04, outputFileName = paste(outfile, "_sample5_az04.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 820000, Ncoef = 1780000 , autozygosity = 0.05, outputFileName = paste(outfile, "_sample5_az05.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 820000, Ncoef = 1780000 , autozygosity = 0.06, outputFileName = paste(outfile, "_sample5_az06.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 820000, Ncoef = 1780000 , autozygosity = 0.07, outputFileName = paste(outfile, "_sample5_az07.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 820000, Ncoef = 1780000 , autozygosity = 0.08, outputFileName = paste(outfile, "_sample5_az08.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 820000, Ncoef = 1780000 , autozygosity = 0.09, outputFileName = paste(outfile, "_sample5_az09.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 820000, Ncoef = 1780000 , autozygosity = 0.10, outputFileName = paste(outfile, "_sample5_az10.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 820000, Ncoef = 1780000 , autozygosity = 0.11, outputFileName = paste(outfile, "_sample5_az11.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 820000, Ncoef = 1780000 , autozygosity = 0.12, outputFileName = paste(outfile, "_sample5_az12.txt", sep = ""))

runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1060000, Ncoef = 1540000 , autozygosity = 0.02, outputFileName = paste(outfile, "_sample6az_02.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1060000, Ncoef = 1540000 , autozygosity = 0.03, outputFileName = paste(outfile, "_sample6az_03.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1060000, Ncoef = 1540000 , autozygosity = 0.04, outputFileName = paste(outfile, "_sample6az_04.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1060000, Ncoef = 1540000 , autozygosity = 0.05, outputFileName = paste(outfile, "_sample6az_05.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1060000, Ncoef = 1540000 , autozygosity = 0.06, outputFileName = paste(outfile, "_sample6az_06.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1060000, Ncoef = 1540000 , autozygosity = 0.07, outputFileName = paste(outfile, "_sample6az_07.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1060000, Ncoef = 1540000 , autozygosity = 0.08, outputFileName = paste(outfile, "_sample6az_08.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1060000, Ncoef = 1540000 , autozygosity = 0.09, outputFileName = paste(outfile, "_sample6az_09.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1060000, Ncoef = 1540000 , autozygosity = 0.10, outputFileName = paste(outfile, "_sample6az_10.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1060000, Ncoef = 1540000 , autozygosity = 0.11, outputFileName = paste(outfile, "_sample6az_11.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1060000, Ncoef = 1540000 , autozygosity = 0.12, outputFileName = paste(outfile, "_sample6az_12.txt", sep = ""))

runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1300000, Ncoef = 1300000 , autozygosity = 0.02, outputFileName = paste(outfile, "_sample7az_02.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1300000, Ncoef = 1300000 , autozygosity = 0.03, outputFileName = paste(outfile, "_sample7az_03.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1300000, Ncoef = 1300000 , autozygosity = 0.04, outputFileName = paste(outfile, "_sample7az_04.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1300000, Ncoef = 1300000 , autozygosity = 0.05, outputFileName = paste(outfile, "_sample7az_05.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1300000, Ncoef = 1300000 , autozygosity = 0.06, outputFileName = paste(outfile, "_sample7az_06.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1300000, Ncoef = 1300000 , autozygosity = 0.07, outputFileName = paste(outfile, "_sample7az_07.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1300000, Ncoef = 1300000 , autozygosity = 0.08, outputFileName = paste(outfile, "_sample7az_08.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1300000, Ncoef = 1300000 , autozygosity = 0.09, outputFileName = paste(outfile, "_sample7az_09.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1300000, Ncoef = 1300000 , autozygosity = 0.10, outputFileName = paste(outfile, "_sample7az_10.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1300000, Ncoef = 1300000 , autozygosity = 0.11, outputFileName = paste(outfile, "_sample7az_11.txt", sep = ""))
runSimulation(hmm = hmmForSim3, variantinfo = siminput, Rcoef = 1300000, Ncoef = 1300000 , autozygosity = 0.12, outputFileName = paste(outfile, "_sample7az_12.txt", sep = ""))
