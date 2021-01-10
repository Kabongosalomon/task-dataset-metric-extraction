#!/usr/bin/env python3

from urllib.request import Request, urlopen
import logging
import hashlib
import hashlib
from functools import partial
import os
import subprocess
import ipdb
import re
from tqdm import tqdm

import arxiv


size_threshold_KB = 50
expected_content_type = "application/pdf"

# Returns the object id
def custom_slugify(obj):
    return obj.get('id').split('/')[-1]

def main():
    f = open("paper_links.tsv", "r")

    already_in_folder = os.listdir("../pdf") 
    links_failed_papers = []
    for line in f:
        line = line.strip()
        name, link = line.split("\t")

        if link.split('/')[-1] in already_in_folder:
            continue 

        link_ID = link[:-4]

        link_pdf = re.sub("abs", "pdf", link_ID)

        paper = {'id': link_ID,\
                    'guidislink': True,\
                        'pdf_url': link_pdf}
        
        try:

            # Download with a specified slugifier function
            arxiv.download(paper, \
                slugify=custom_slugify, dirpath='../pdf')

            print("Downloading " + name + "... ", end = '', flush=True)
            
            # req = Request(link)
            # req.add_header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
            # pdffile = urlopen(req)
            # content_type = pdffile.info().get('Content-Type')

            # with open(name,'wb') as output:
            #     output.write(pdffile.read())
            # subprocess.run(["wget", link + " -O " + name])



            # print("Done.", flush=True)
            
            # compute checksum on the downloaded file
            # downloaded_checksum = md5(name)
            # if downloaded_checksum != md5checksum:
            #     size = filesizeKB(name)
            #     print("warning: Wrong MD5 checksum. Size of the downloaded PDF: %dKB" % (size))
            #     if content_type != expected_content_type or size < size_threshold_KB:
            #         links_failed_papers.append((name, link))
            #         if content_type != expected_content_type:
            #             print("Expected %s MIME type, instead got %s" % (expected_content_type, content_type))
        except Exception as e:
            logging.exception("Failed.");
    f.close()
    if links_failed_papers:
        print("The downloads from the following links likely failed. You might want to check them and download the PDFs manually.")
        for name, link in links_failed_papers:
            print(name + " from " + link)
    else:
        print("Complete.")
    
def filesizeKB(filename):
    return os.path.getsize(filename) // 1024

# def md5(filename):
#     with open(filename, mode='rb') as f:
#         d = hashlib.md5()
#         for buf in iter(partial(f.read, 4096), b''):
#             d.update(buf)
#     return d.hexdigest()
    

main()