import os

#Absolute path to a directory containing all of your .java files
dir = "/mnt/k/dev/project2/src"

#absolute path to a folder containing test case files
input_dir = "/mnt/k/dev/project2/input_grade"

# list of test case files
inputs = os.listdir(input_dir)

main = False
folder = ""
print(dir)
file = list(os.walk(dir))[0]
# if "Main.class" in file[2]:
#     found += 1
# else:
#     print(file[0])
os.system(" cd "+ dir + " ; mkdir results")
os.system(" cd "+ dir + " ; javac Main.java")
for inputx in inputs:
    # os.system("cd "+ dir +" ; touch ./results/"+ inputx + "")
    os.system(" cd "+ dir + "; touch ./results/" + inputx + "_AVL.txt")
    os.system(" cd "+ dir + "; touch ./results/" + inputx + "_BST.txt")
    os.system(" cd "+ dir + " ; java  Main   /mnt/k/dev/project2/input_grade/" + inputx + "    "  + "./results/" + inputx  +  " > ./log.txt" )
print("\n")

main = False
folder = ""
# print(dir)
file = list(os.walk(dir))[0]
# if "Main.class" in file[2]:
#     found += 1
# else:
os.system("rm " + dir + "/grade.txt")
results = open(dir + "/grade.txt", "w")
grade = 0
for inputx in inputs:
    skip = False
    try: 
        f_theirs = open(dir + "/results/"+  inputx + "_avl.txt", "r") 
        f_mine =   open("/mnt/k/dev/project2/output_grade/" + inputx + "_avl.txt", "r")
        lines = list(f_theirs)
        correct_lines = list(f_mine)
    except:
        print(dir + " AVL failed")
        skip = True
    if not skip:
        results.write(inputx + " AVL :\n\n")
        mismatch = False
        if len(lines) != len(correct_lines):

            results.write("Size mismatch\n")
            mismatch = True
        else:
            
            for i in range(len(correct_lines)):
                lines[i].strip()
                lines[i].lower()
                correct_lines[i].strip()
                correct_lines[i].lower()
                if not lines[i].lower().split() == correct_lines[i].lower().split():
                    results.write("Correct Line:\n")
                    results.write(correct_lines[i])
                    results.write("Your Line Line:\n")
                    results.write(lines[i]+ " \n\n")
                    mismatch = True
        if not mismatch: 
            if inputx.find("input") > -1:
                results.write("6 points granted\n")
                grade += 6
            else:
                results.write("3 points granted\n")
                grade += 3

        f_mine.close()
        f_theirs.close()

    skip = False
    try:
        f_theirs = open(dir + "/results/" + inputx + "_BST.txt", "r") 
        f_mine =   open("/mnt/k/dev/project2/output_grade/" + inputx + "_BST.txt", "r")
        lines = list(f_theirs)
        correct_lines = list(f_mine)
    except:
        print(dir + " BST failed")
        skip = True
    if len(lines) == 0:
        skip = True
    if not skip:
        results.write(inputx + " BST :\n\n")
        mismatch = False
        # if len(lines[-1].strip()) == 0:
        #     lines.remove(lines[-1])
        # if len(correct_lines[-1].strip()) == 0:
        #     correct_lines.remove(correct_lines[-1])
        if len(lines) != len(correct_lines):

            results.write("Size mismatch\n")
            mismatch = True
        else:
            
            for i in range(len(correct_lines)):
                lines[i].strip()
                lines[i].lower()
                correct_lines[i].strip()
                correct_lines[i].lower()
                if not lines[i].lower().split() == correct_lines[i].lower().split():
                    results.write("Correct Line:\n")
                    results.write(correct_lines[i])
                    results.write("Your Line Line:\n")
                    results.write(lines[i]+ " \n\n")
                    mismatch = True
        if not mismatch: 
            if inputx.find("input") > -1:
                results.write("6 points granted\n")
                grade += 6
            else:
                results.write("2 points granted\n")
                grade += 2

        f_mine.close()
        f_theirs.close()

results.write("Total score :" + str(grade))
print(dir , grade)
results.close()