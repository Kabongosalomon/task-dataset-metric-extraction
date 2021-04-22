import json
import requests
import ipdb
from collections import defaultdict
import os


implementations = {}
notfound = []
tasks = {}
categories = {}
evaluations = {}
datasets = {}
counter = 1112
metrics = {}
models = {}

TDM_taxonomy = defaultdict(lambda: 0 )
TDMs_taxonomy = defaultdict(lambda: 0 )

def readFile(path):
    """
    Reads the json file and returns a dictionary containing the json object
    
    Parameters
    ----------
    path : str
        The local path of the json file
    """
    
    file = open(path)
    json_string = json.load(file)
    file.close()
    return json_string


# this is to keep track of occurance of a leaderboard  
def get_TDM_taxonomy(task, dataset, metric):
    TDM_taxonomy[task+"#"+dataset+"#"+metric] += 1

def get_TDMs_taxonomy(task, dataset, metric, score):
    TDMs_taxonomy[task+"#"+dataset+"#"+metric+"#"+score] += 1

def get_title_taxonomy(paper_title):
    paper_name_taxonomy[paper_title] += 1

def parse_TDM_taxonomy(TDM_taxonomy):      
    with open("./TDM_taxonomy.tsv", "a+", encoding="utf-8") as text_file:
        for key, value in TDM_taxonomy.items():
            text_file.write(key+"\t"+str(value)+"\n")

def parse_TDMs_taxonomy(TDMs_taxonomy):      
    with open("./TDMs_taxonomy.tsv", "a+", encoding="utf-8") as text_file:
        for key, value in TDMs_taxonomy.items():
            text_file.write(key+"\t"+str(value)+"\n")

def parse_title_taxonomy(paper_name_taxonomy):
    with open("./paper_name_taxonomy.tsv", "a+", encoding="utf-8") as text_file:
        for key, value in paper_name_taxonomy.items():
            text_file.write(key+"\t"+str(value)+"\n")

def resultsAnnotation(paper_name, task, dataset, metric, score):
    path = "./resultsAnnotation.tsv"
    with open(path, "a+", encoding="utf-8") as text_file:

        if paper_name not in paper_name_taxonomy:
            # if first:
            text_file.write(paper_name+"\t"+task+"#"+dataset+"#"+metric+"#"+score+"\n")
        else:
            # TODO: This approach is not optimal nor scalable, need to redo this
            with open(path, 'r',encoding="utf-8") as file:
                # read a list of lines into data
                data = file.readlines()

            for i, key in enumerate(reversed(data)):
                if key.split("\t")[0] == paper_name:
                    data[len(data)-i-1] = data[len(data)-i-1].replace("\n", '')+\
                            '$'+task+"#"+dataset+"#"+metric+"#"+score+"\n"
                    break

            # # and write everything back
            with open(path, 'w', encoding="utf-8") as file:
                file.writelines( data )

        # this is to keep track of occurance of a leaderboard     
        get_TDM_taxonomy(task, dataset, metric)
        get_TDMs_taxonomy(task, dataset, metric, score)

        paper_name_taxonomy[paper_name]+=1


def datasetAnnotation(paper_title, dataset):      

    path = "./datasetAnnotation.tsv"

    with open(path, "a+", encoding="utf-8") as text_file:

        if paper_title not in paper_title_taxonomy:
            # if first:
            text_file.write(paper_title+"\t"+dataset+"\n")
        else:
            # TODO: This approach is not optimal nor scalable, need to redo this
            with open(path, 'r',encoding="utf-8") as file:
                # read a list of lines into data
                data = file.readlines()

            for i, key in enumerate(reversed(data)):
                if key.split("\t")[0] == paper_title:
                    data[len(data)-i-1] = data[len(data)-i-1].replace("\n", '')+\
                            '#'+dataset+"\n"
                    break

            # # and write everything back
            with open(path, 'w', encoding="utf-8") as file:
                file.writelines( data )

        paper_title_taxonomy[paper_title]+=1


def taskAnnotation(paper_title, task):     
    path = "./taskAnnotation.tsv"
    if paper_title not in paper_title_taskAnnotation: 
        with open(path, "a+", encoding="utf-8") as text_file:
            text_file.write(paper_title+"\t"+task)
            text_file.write("\n")
        paper_title_taskAnnotation[paper_title]+=1
        

def paper_links(paper_title, paper_url):      
    with open("./paper_links.tsv", "a+", encoding="utf-8") as text_file:
        text_file.write(paper_title+"\t"+paper_url)
        text_file.write("\n")


def parseTask(obj):
    """
    parses the Task json object and add the information into the graph
    
    Note: it might be called recursively
    
    Parameters
    ----------
    obj : dict
        the json representation of the Task object
    """

    datasets = obj["datasets"]

    task =  obj["task"]

    for dt in datasets:
        dataset = dt["dataset"]
        

        for _, row in enumerate(dt["sota"]["rows"]):
            
            paper_url = row['paper_url']

            if paper_url == "" or paper_url[-4:] == "html":
                continue

            if paper_url[-1] == '/':
                paper_url = paper_url[:-1]+'.pdf'

            elif paper_url[-3:] == "pdf":
                paper_url = paper_url
            else:
                paper_url = paper_url+'.pdf'

            paper_title = paper_url.split('/')[-1]

            # if (paper_title in paper_name_taxonomy) or (paper_url.split("//")[1][:5] != 'arxiv'):
            #     # TODO need to find a clever way to deal with the case of repeated paper
            #     # at different part of the json
            #     continue

            if (paper_url.split("//")[1][:5] != 'arxiv'):
                # TODO need to find a clever way to deal with paper not from arxiv
                continue

            # TODO I need to improve this for many metrics 
            for i, (metric, score) in  enumerate(row['metrics'].items()):
                # metric = dt["sota"]["metrics"][0]
                # rows = dt["sota"]["rows"]
                # not all the metrics in sota are in metrics rows

                resultsAnnotation(paper_title, task, \
                                  dataset, metric, score)



            taskAnnotation(paper_title, task)

            datasetAnnotation(paper_title, dataset)

            paper_links(paper_title, paper_url)

    subtasks = obj["subtasks"]

    for subtask in subtasks:
        parseTask(subtask)

        
    
def createEvaluationSubgraph(obj):
    """
    Create the subgraph relating to the provided json strucutre,
    this subgraph corresponds to one of the paperswithcode data files
    (evaluation-tables.json').
    
    Parameters
    ----------
    obj : dict
        the json representation of the eu.tib.sre.evaluation of the paper
    """
    parseTask(obj)
    

paper_name_taxonomy = defaultdict(lambda : 0)
paper_title_taxonomy = defaultdict(lambda : 0)
paper_title_taskAnnotation = defaultdict(lambda : 0)

if __name__ == '__main__':
    """
    The current version of the code support only open access paper that are available on arxiv, 
    
    TODO
    -----
    - we can modify the code to support other open access source 
    """
    # papersWithAbstracts = readFile('source_files/papers-with-abstracts.json')
    # papersWithCode = readFile('source_files/links-between-papers-and-code.json')
    evalTables = readFile('source_files/evaluation-tables.json')

    filePath_result = "./resultsAnnotation.tsv"
    filePath_dataset = "./datasetAnnotation.tsv"
    filePath_task = "./taskAnnotation.tsv"
    filePath_links = "./paper_links.tsv"
    filePath_TDM = "./TDM_taxonomy.tsv"
    filePath_TDMs = "./TDMs_taxonomy.tsv"
    filePath_PaperName = "./paper_name_taxonomy.tsv"


    for filePath in [filePath_result, filePath_dataset, \
                    filePath_task, filePath_links, filePath_TDM, filePath_TDMs, filePath_PaperName]:
        if os.path.exists(filePath):
            os.remove(filePath)
    
    for entry in evalTables:
        createEvaluationSubgraph(entry)
   #-------------------------------------------------

    # Saving the file from default dic to .tsv 
    parse_TDM_taxonomy(TDM_taxonomy)
    parse_TDMs_taxonomy(TDMs_taxonomy)
    parse_title_taxonomy(paper_name_taxonomy)

    # Clean the previously downloaded files for a minimum number of paper 



       