import json
import os


folder_path = "/Users/ian/Documents/PROJECT/els/1.자료/전체 내려받기_우리말샘_json_20240605"
ids = set()
words = []

for filename in os.listdir(folder_path):
    if filename.endswith(".json"):  # 파일이 .json 확장자로 끝나는지 확인
        file_path = os.path.join(folder_path, filename)
        with open(file_path, 'r') as file:
            data = json.load(file)

        for word in data['channel']['item']:
            if(word['target_code'] in ids):
                print(word['target_code'])
            else:
                ids.add(word['target_code'])
                words.append(word)

with open('./words.json','w') as file:
    json.dump(words,file,indent=4)
