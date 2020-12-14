import json
import requests
import ipdb
from collections import defaultdict


# base_url='http://localhost:8000/api/'
predicates = {}
implementations = {}
resources = {}
notfound = []
tasks = {}
categories = {}
evaluations = {}
datasets = {}
counter = 1112
metrics = {}
models = {}

TDM_taxonomy = defaultdict(lambda: 0 )

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

def createResource(label):
    """
    Create a new resoruce using the provided label
    
    Parameters
    ----------
    label : str
        The label of the resource
    """
    
    data = {"label" : label}
    return createPost(base_url+'resources/',data).json()
    return data

def createLiteral(label):
    """
    Create a new literal using the provided label
    
    Parameters
    ----------
    label : str
        The label of the literal
    """
    data = {"label" : label}
    # return createPost(base_url+'literals/',data).json()
    return data

def createPredicate(label):
    """
    Create a new predicate using the provided label
    
    Parameters
    ----------
    label : str
        The label of the predicate
    """
    data = {"label" : label}
    # return createPost(base_url+'predicates/',data).json()
    return data

def createStatement(sub, pred, obj):
    """
    Create a literal statment using the provided subject, predicate, and object
    
    Parameters
    ----------
    sub : int
        The id of the subject resource
    pred : int
        The id of the predicate
    obj : int
        The id of the literal resource to be placed in the object position
    """
    data = {"subject_id" : sub, "predicate_id" : pred, "object": { "id": obj, "_class": "literal" } }

    # resp = requests.post('{}statements/'.format(base_url), json=data)
    # if resp.status_code != 201:
    #     print(resp.text)
    # return resp

    return data

def createResourceStatement(sub, pred, obj):
    """
    Create a literal statment using the provided subject, predicate, and object
    
    Parameters
    ----------
    sub : int
        The id of the subject resource
    pred : int
        The id of the predicate
    obj : int
        The id of the object resource (can't be a literal)
    """
    data = {"subject_id" : sub, "predicate_id" : pred, "object": { "id": obj, "_class": "resource" } }
    # resp = requests.post('{}statements/'.format(base_url), json=data)
    # if resp.status_code != 201:
    #     print(resp.text)
    # return resp
    return data

# def createPost(url, data):
#     """
#     Internal call for the API
    
#     Parameters
#     ----------
#     url : str
#         the url of the api endpoint
#     data : dict
#         the json data as a dictionary object
#     """
#     resp = requests.post(url, json = data)
#     if resp.status_code != 201:
#         print(resp.text)
#     return resp

def createOrFindPredicate(label):
    """
    Create a new predicate but not before looking it up in the in-memory list of created predicates
    (should be imporved to llok up in the neo4j to avoid duplicate predicates)
    
    Parameters
    ----------
    label : str
        The label of the predicate
    """
    if label in predicates:
        return predicates[label]
    else:
        pred = createPredicate(label)
        predicates[pred['label']]=pred['id']
        return pred['id']

def createPaperSubgraph(obj):
    """
    Create the subgraph relating to the provided json strucutre,
    this subgraph corresponds to one of the paperswithcode data files
     ('papers-with-abstracts.json')
    
    Parameters
    ----------
    obj : dict
        the json representation of the paper
    """
    arxiv_id = obj["arxiv_id"]
    title = obj["title"]
    abstract = obj["abstract"]
    url = obj["url_pdf"]
    proceeding = obj["proceeding"]

    tasks = obj["tasks"]
    
    return {title : {'url_pdf': url, 'tasks':tasks, 'dataset': [], 'metric': [], 'score': []}}
def createCodeSubgraph(obj):
    """
    Create the subgraph relating to the provided json strucutre,
    this subgraph corresponds to one of the paperswithcode data files
    ('links-between-papers-and-code.json'), it creates an implementation node
    and appends the repo url to that node
    
    Note: it needs to be imporved to lookup in the Neo4J if the implementation node already exists
    
    Parameters
    ----------
    obj : dict
        the json representation of the paper
    """
    title = obj["paper_title"]
    repo_url = obj["repo_url"]
    if title not in resources:
        print("resource not found")
        notfound.append(title)
        return
    if title in implementations:
        impl_id = implementations[title]
    else:
        impl_id = createResource("(Implementation) {}".format(title))['id']
        implementations[title] = impl_id
        impl_pred = createOrFindPredicate('has implementation')
        createResourceStatement(resources[title],impl_pred,impl_id)
    p_repo_url = createOrFindPredicate('has repo url')
    l_repo_url = createLiteral(repo_url)['id']
    createStatement(impl_id,p_repo_url,l_repo_url)
    #print("Implementation added for ({})".format(title))
    
def findPaperInMemory(title, collection, key):
    """
    Find a paper json object in the in-memory json representation lists
    
    Returns the json dict object if found, and NoneType otherwise
    
    Parameters
    ----------
    title : str
        the title of the paper in question
    collection : list
        the json list object to be searched in
    key : str
        the key to use to look up the title and compare it
    """
    for paper in collection:
        if paper[key] == title:
            return paper
    return None

def createOrFindMetric(metric):
    """
    Creates a new resource representing the metric used or finds it in the in-memory storage
    (should be imporved to look up in the neo4j to avoid duplicate predicates)
    
    Parameters
    ----------
    metric : str
        The label of the predicate
    """
    if metric in metrics:
        return metrics[metric]
    else:
        metric_id = createResource(metric)["id"]
        metrics[metric] = metric_id
        return metric_id
    
def createOrFindModel(model):
    """
    Creates a new resource representing the model used or finds it in the in-memory storage
    (should be imporved to look up in the neo4j to avoid duplicate predicates)
    
    Parameters
    ----------
    model : str
        The label of the predicate
    """
    if model in models:
        return models[model]
    else:
        model_id = createResource(model)["id"]
        models[model] = model_id
        return model_id
    
def parseDataset(obj):
    """
    parses the Dataset json object and add the information into the graph
    
    Parameters
    ----------
    obj : dict
        the json representation of the Task
    """
    global counter
    dataset_name = obj["dataset"]
    dataset_description = obj["description"]
    if dataset_name not in datasets:
        dataset_id = createResource(dataset_name)["id"]
        datasets[dataset_name] = dataset_id
        if is_not_blank(dataset_description):
            desc_id = createLiteral(dataset_description)["id"]
            pred_id = createOrFindPredicate("has description")
            createStatement(dataset_id, pred_id, desc_id)
    else:
        dataset_id = datasets[dataset_name]
    if "sota" not in obj:
        return
    sota = obj["sota"]
    for sota_row in sota["sota_rows"]:
        model_name = sota_row["model_name"]
        paper = sota_row["paper_title"]
        if not is_not_blank(paper):
            continue
        if paper in resources:
            paper_id = resources[paper]
        else:
            paper_id = createResource(paper)["id"]
        if paper in evaluations:
            eval_id = evaluations[paper]
        else:
            eval_id = createResource("(Evaluation) {}".format(paper))['id']
            evaluations[paper] = eval_id
            pred_id = createOrFindPredicate("has eu.tib.sre.evaluation")
            createResourceStatement(paper_id, pred_id, eval_id)
        for key, value in sota_row["metrics"].items():
            res_id = createResource("Res_{}".format(counter))["id"]
            counter+=1
            pred_id = createOrFindPredicate("has result")
            createResourceStatement(eval_id, pred_id, res_id)
            metric_id = createOrFindMetric(key)
            m_pred_id = createOrFindPredicate("has metric")
            value_id = createLiteral(value)["id"]
            v_pred_id = createOrFindPredicate("has value")
            createResourceStatement(res_id, m_pred_id, metric_id)
            createStatement(res_id, v_pred_id, value_id)
            model_id = createOrFindModel(model_name)
            pred_id = createOrFindPredicate("on model")
            createResourceStatement(res_id, pred_id, model_id)
            pred_id = createOrFindPredicate("using dataset")
            createResourceStatement(res_id, pred_id, dataset_id)


# this is to keep track of occurance of a leaderboard  
def get_TDM_taxonomy(task, dataset, metric, score):
    TDM_taxonomy[task+"#"+dataset+"#"+metric] += 1

def get_title_taxonomy(paper_title):
    paper_name_taxonomy[paper_title] += 1


def resultsAnnotation(paper_name, task, dataset, metric, score):
    path = "./annotations/resultsAnnotation.tsv"
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
        TDM_taxonomy[task+"#"+dataset+"#"+metric]+=1

        paper_name_taxonomy[paper_name]+=1

def parse_TDM_taxonomy(TDM_taxonomy):      
    with open("./annotations/TDM_taxonomy.tsv", "a+", encoding="utf-8") as text_file:
        for key, value in TDM_taxonomy.items():
            text_file.write(key+"\t"+str(value)+"\n")

def parse_title_taxonomy(paper_name_taxonomy):
    with open("./annotations/paper_name_taxonomy.tsv", "a+", encoding="utf-8") as text_file:
        for key, value in paper_name_taxonomy.items():
            text_file.write(key+"\t"+str(value)+"\n")
        

def datasetAnnotation(paper_title, dataset, first=True):      
    with open("./annotations/datasetAnnotation.tsv", "a+", encoding="utf-8") as text_file:
        if first:
            text_file.write(paper_title+"\t"+dataset)
            text_file.write("\n")
        else:
            text_file.write("#")   
            text_file.write(dataset) 

def taskAnnotation(paper_title, task, first=True):      
    with open("./annotations/taskAnnotation.tsv", "a+", encoding="utf-8") as text_file:
        if first:
            text_file.write(paper_title+"\t"+task)
            text_file.write("\n")
        else:
            text_file.write("#")   
            text_file.write(task) 

def paper_links(paper_title, paper_url):      
    with open("./downloader/paper_links.tsv", "a+", encoding="utf-8") as text_file:
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



            taskAnnotation(paper_title, task, first=True)

            datasetAnnotation(paper_title, dataset, first=True)

            paper_links(paper_title, paper_url)

    subtasks = obj["subtasks"]

    for subtask in subtasks:
        parseTask(subtask)

        
    
def createEvaluationSubgraph(obj):
    """
    Create the subgraph relating to the provided json strucutre,
    this subgraph corresponds to one of the paperswithcode data files
    ('eu.tib.sre.evaluation-tables.json'), it creates an eu.tib.sre.evaluation node
    with all related information
    
    Parameters
    ----------
    obj : dict
        the json representation of the eu.tib.sre.evaluation of the paper
    """
    parseTask(obj)
    
def is_not_blank(string):
    """
    Checks if string is not empty
    
    print is_not_blank("")    # False
    print is_not_blank("   ") # False
    print is_not_blank("ok")  # True
    print is_not_blank(None)  # False
    
    Parameters
    ----------
    string : str
        the string to be checked
    """
    return bool(string and string.strip())

import  os

paper_name_taxonomy = defaultdict(lambda : 0)


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

    filePath_result = "./annotations/resultsAnnotation.tsv"
    filePath_dataset = "./annotations/datasetAnnotation.tsv"
    filePath_task = "./annotations/taskAnnotation.tsv"
    filePath_links = "./downloader/paper_links.tsv"
    filePath_TDM = "./annotations/TDM_taxonomy.tsv"
    filePath_PaperName = "./annotations/paper_name_taxonomy.tsv"


    for filePath in [filePath_result, filePath_dataset, \
                    filePath_task, filePath_links, filePath_TDM, filePath_PaperName]:
        if os.path.exists(filePath):
            os.remove(filePath)
    
    for entry in evalTables:
        createEvaluationSubgraph(entry)
   #-------------------------------------------------

    # Saving the file from default dic to .tsv 
    parse_TDM_taxonomy(TDM_taxonomy)
    parse_title_taxonomy(paper_name_taxonomy)

    # Clean the previously downloaded files for a minimum number of paper 



       