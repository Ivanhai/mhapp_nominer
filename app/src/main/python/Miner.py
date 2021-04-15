username = "httsmvkcom"
efficiency = 100
autorestart = 30
debug = False

#!/usr/bin/env python3
import socket, statistics, threading, time, re, subprocess, hashlib, sys, datetime, os # Import libraries
from pathlib import Path
from signal import signal, SIGINT

def install(package):
  subprocess.check_call([sys.executable, "-m", "pip", "install", package])
  os.execl(sys.executable, sys.executable, *sys.argv)

def now():
  return datetime.datetime.now()

try: # Check if cpuinfo is installed
  import cpuinfo
  from multiprocessing import freeze_support
except:
  print(now().strftime("%H:%M:%S ") + "Cpuinfo не установлен. Майнер попробует установить его. Команда для ручной установки: pip3 install py-cpuinfo")
  install("py-cpuinfo")
  
try: # Check if colorama is installed
  from colorama import init, Fore, Back, Style
except:
  print(now().strftime("%H:%M:%S ") + "Colorama не установлен. Майнер попробует установить его. Команда для ручной установки: pip3 install colorama")
  install("colorama")

try: # Check if requests is installed
  import requests
except:
  print(now().strftime("%H:%M:%S ") + "Requests не установлен. Майнер попробует установить его. Команда для ручной установки: pip3 install requests")
  install("requests")

# Global variables
minerVersion = "1.3" # Version number
timeout = 5 # Socket timeout
shares = [0, 0]
diff = 0
last_hash_count = 0
khash_count = 0
hash_count = 0
hash_mean = []
efficiency = (100 - float(efficiency)) * 0.01 # Calulate efficiency for use with sleep function
serveripfile = "https://mhcoin.s3.filebase.com/Pool.txt" # Serverip file
donationlevel = 0
freeze_support() # If not used, pyinstaller hangs when checking cpuinfo
cpu = cpuinfo.get_cpu_info() # Processor info
logg = ""

def debugOutput(text):
  global logg
  if debug == "True":
    logg = logg + now().strftime("%H:%M:%S.%f ") + "DEBUG: " + text + "\n"

# def title(title):
#   if os.name == 'nt':
#     os.system("title "+title)
#   else:
#     print('\33]0;'+title+'\a', end='')
#     sys.stdout.flush()

#def handler(signal_received, frame): # If CTRL+C or SIGINT received, send CLOSE request to server in order to exit gracefully.
#  logg = logg + now().strftime("\n%H:%M:%S ") + " sys "
#  + " Закрываю майнер...\n"
#  try:
#    soc.close()
#  except:
#    pass
#  os._exit(0)
#signal(SIGINT, handler) # Enable signal handler

def Greeting(): # Greeting message depending on time
  global autorestart, greeting, logg
  #print(Style.RESET_ALL)

  if float(autorestart) <= 0:
    autorestart = 0
    autorestartmessage = "disabled"
  if float(autorestart) > 0:
    autorestartmessage = "каждые " + str(autorestart) + " минут"

  current_hour = time.strptime(time.ctime(time.time())).tm_hour

  if current_hour < 12 :
    greeting = "Доброе утро"
  elif current_hour == 12 :
    greeting = "Счастливого полудня"
  elif current_hour > 12 and current_hour < 18 :
    greeting = "Удачного вечера"
  elif current_hour >= 18 :
    greeting = "Спокойной ночи"
  else:
    greeting = "Добро пожаловать"

  privetstv = " > " + "MHCoin miner"
  telega = " > " + "https://t.me/joinchat/S1TCw6SLQjn_8Enf\n"
  logg = logg + privetstv
  logg = logg + telega
  #try:
  #  print(" > " + Fore.WHITE + "CPU: " + Style.BRIGHT + Fore.YELLOW + str(cpu["brand_raw"]))
  #except:
  #  if debug == "True": raise
  if os.name == 'nt' or os.name == 'posix':
    logg = logg + " > " + "Алгоритм: " + "DUCO-S1\n"
    logg = logg + " > " + "Перезапуск: " + str(autorestartmessage) + "\n"
    logg = logg + " > " + str(greeting) + ", " + str(username) + "!\n"

def hashrateCalculator(): # Hashes/sec calculation
  global last_hash_count, hash_count, khash_count, hash_mean
  last_hash_count = hash_count
  khash_count = last_hash_count / 1000
  hash_mean.append(khash_count) # Calculate average hashrate
  khash_count = round(statistics.mean(hash_mean), 3)
  hash_count = 0 # Reset counter
  threading.Timer(1.0, hashrateCalculator).start() # Run this def every 1s

def autorestarter(): # Autorestarter
  time.sleep(float(autorestart)*60)
  logg = logg + now().strftime("%H:%M:%S ") + " sys "
  + " Autorestarting the miner"
  os.execl(sys.executable, sys.executable, *sys.argv)
def Connect(): # Connect to master server section
  global soc, masterServer_address, masterServer_port
  try:
    res = requests.get(serveripfile, data = None) # Use request to grab data from raw github file
    if res.status_code == 200: # Check for response
      content = res.content.decode().splitlines() # Read content and split into lines
      masterServer_address = content[0] # Line 1 = pool address
      masterServer_port = content[1] # Line 2 = pool port
      debugOutput("Получен IP Сервера: " + masterServer_address + ":" + str(masterServer_port))
  except: # If it wasn't, display a message
    logg = logg + now().strftime("%H:%M:%S ") + " net "
    + " Ошибка получения IP сервера! Перезапуск через 10 секунд.\n"
    if debug == "True": raise
    time.sleep(10)
    Connect()
  try: # Try to connect
    try: # Shutdown previous connections (if any)
      soc.shutdown(socket.SHUT_RDWR)
      soc.close()
    except:
      debugOutput("No previous connections to close")
    soc = socket.socket()
    soc.connect((str(masterServer_address), int(masterServer_port)))
    soc.settimeout(timeout)
  except: # If it wasn't, display a message
    logg = logg + now().strftime("%H:%M:%S ") + " net "
    + " Ошибка подключения к серверу! Перезапуск через 10 секунд.\n"
    if debug == "True": raise
    time.sleep(10)
    Connect()

def checkVersion():
  global logg
  serverVersion = soc.recv(3).decode() # Check server version
  debugOutput("Версия сервера: " + serverVersion)
  if float(serverVersion) <= float(minerVersion) and len(serverVersion) == 3: # If miner is up-to-date, display a message and continue
    logg = logg + str(now().strftime("%H:%M:%S ") + " net "
    + " Подключено" + " к серверу MHCoin (v"+str(serverVersion)+")\n")
  else:
    logg = logg + str(now().strftime("%H:%M:%S ") + " sys "
      + " Miner is outdated (v"+minerVersion+"),"
      + " server is on v"+serverVersion+", please download latest version from https://github.com/revoxhere/duino-coin/releases/ or type \'continue\' if you wish to continue anyway.\n")

def Mine(): # Mining section
  global last_hash_count, hash_count, khash_count, donationlevel, efficiency, logg

  if int(donationlevel) <= 0:
    logg = logg + str(now().strftime("%H:%M:%S ") + " sys "
    + " MHCoin бесплатный и будет всегда таким."
    + "\nМы не берем комиссии с майнинга и транзакций, вы можете поддержать нас задонатив.\n"
    + "XMR: 4B3CSkBQABAUgVWwPn6SvSUjK9QV9cAt6LEWxYbz43G8eqGv5Yey5ieDJPVwnyHKK1Jpe1FC4Wo4yAXDM7WBa1pGPPdUZYf\n")
  
  logg = logg + str(now().strftime("%H:%M:%S ") + " sys "
  + " Майнер запущен"
  + " используя алгоритм DUCO-S1 с " + str(100-(100*int(efficiency))) + "% эффективности.\n")
  while True:
    if float(efficiency) < 100: time.sleep(float(efficiency)) # Sleep to achieve lower efficiency if less than 100 selected
    while True:
      try:
        soc.send(bytes(f"JOB,{str(username)}", encoding="utf8")) # Send job request
        job = soc.recv(1024).decode() # Get work from pool
        job = job.split(",") # Split received data to job and difficulty
        diff = job[2]
        if job[0] and job[1] and job[2]:
          debugOutput("Job received: " +str(job))
          break # If job received, continue to hashing algo
      except:
        Connect()
        break

    for ducos1res in range(100 * int(diff) + 1): # Loop from 1 too 100*diff)
      ducos1 = hashlib.sha1(str(job[0] + str(ducos1res)).encode("utf-8")).hexdigest() # Generate hash
      hash_count = hash_count + 1 # Increment hash counter
      if job[1] == ducos1: # If result is even with job, send the result
        debugOutput("Result found: " + str(ducos1res))
        while True:
          try:
            soc.send(bytes(f"{str(ducos1res)},{str(khash_count*1000)},Repl.it Miner v{str(minerVersion)}", encoding="utf8")) # Send result of hashing algorithm to pool
            responsetimetart = now()
            feedback = soc.recv(128).decode() # Get feedback
            responsetimestop = now() # Measure server ping
            ping = responsetimestop - responsetimetart # Calculate ping
            ping = str(int(ping.microseconds / 1000)) # Convert to ms
            debugOutput("Feedback received: " + str(feedback))
          except socket.timeout:
            Connect()

          if feedback == "GOOD": # If result was good
            shares[0] += 1 # Share accepted = increment feedback shares counter by 1
            logg = logg + "MHCoin Offical Miner (v"+str(minerVersion)+") - " + str(shares[0]) + "/" + str(shares[0] + shares[1])+ " accepted shares\n"
            logg = logg + str(now().strftime("%H:%M:%S ") + " cpu "
            + " Accepted " + str(shares[0]) + "/" + str(shares[0] + shares[1])
            + " (" + str(int((shares[0] / (shares[0] + shares[1]) * 100))) + "%)"
            + " ⁃ " + str(khash_count) + " kH/s"
            + " @ diff " + str(diff) + " ⁃ " + "ping " + ping + "ms\n")
            break # Repeat

          elif feedback == "BLOCK": # If block was found
            shares[0] += 1 # Share accepted = increment feedback shares counter by 1
            logg = logg + "MHCoin Offical Miner (v"+str(minerVersion)+") - " + str(shares[0]) + "/" + str(shares[0] + shares[1]) + " accepted shares\n"
            logg = logg + str(now().strftime("%H:%M:%S ") + " cpu "
            + " Block found " + str(shares[0]) + "/" + str(shares[0] + shares[1])
            + " (" + str(int((shares[0] / (shares[0] + shares[1]) * 100))) + "%)"
            + " ⁃ " + str(khash_count) + " kH/s"
            + " @ diff " + str(diff) + " ⁃ " + "ping " + ping + "ms\n")
            break # Repeat

          elif feedback == "INVU": # If this user doesn't exist 
            logg = logg + str(now().strftime("%H:%M:%S ") + " net "
            + " User "+str(username)+" doesn't exist."
            + " Make sure you've entered the username correctly. Please check your config file. Retrying in 10s\n")
            time.sleep(10)
            Connect()

          elif feedback == "ERR": # If server reports internal error
            logg = logg + str(now().strftime("%H:%M:%S ") + " net ")
            + " Internal server error." + " Retrying in 10s\n"
            time.sleep(10)
            Connect()

          else: # If result was bad
            shares[1] += 1 # Share rejected = increment bad shares counter by 1
            logg = logg + "MHCoin Offical Miner (v"+str(minerVersion)+") - " + str(shares[0]) + "/" + str(shares[0] + shares[1]) + " accepted shares"
            logg = logg + str(now().strftime("%H:%M:%S ") + " cpu "
            + " Rejected " + str(shares[0]) + "/" + str(shares[0] + shares[1])
            + " (" + str(int((shares[0] / (shares[0] + shares[1]) * 100))) + "%)"
            + " ⁃ " + str(khash_count) + " kH/s"
            + " @ diff " + str(diff) + " ⁃ " + "ping " + ping + "ms\n")
            break # Repeat
        break # Repeat

def main(usernae):
  global username, logg
  username = str(usernae)
  init(autoreset=True) # Enable colorama
  hashrateCalculator() # Start hashrate calculator
  #title("MHCoin Offical Miner (v"+str(minerVersion)+")")
  try:
    Greeting() # Display greeting message
    debugOutput("Greeting displayed")
  except:
    if debug == "True": raise

  while True:
    try: # Setup autorestarter
      if float(autorestart) > 0:
        debugOutput("Включен перезапуск каждые " + str(autorestart) + " минут")
        threading.Thread(target=autorestarter).start()
      else:
        debugOutput("Перезапуск отключен")
    except:
      logg = logg + now().strftime(Style.DIM + "%H:%M:%S ") + " sys "
      + " Error in the autorestarter. Check configuration file ("+resources+"/Miner_config.cfg). Exiting in 10s\n"
      if debug == "True": raise
      time.sleep(10)
      os._exit(1)

    try:
      Connect() # Connect to pool
      debugOutput("Connected to master server")
    except:
      logg = logg + now().strftime("%H:%M:%S ") + " net "
      + " Ошибка подключения к серверу. Переподключение через 10 секунд.\n"
      if debug == "True": raise
      time.sleep(10)
      Connect()

    try:
      checkVersion() # Check version
      debugOutput("Version check complete")
    except:
      logg = logg + now().strftime("%H:%M:%S ") + " net "
      + " Error checking server version. Retrying in 10s\n"
      if debug == "True": raise
      time.sleep(10)
      Connect()

    try:
      debugOutput("Майнинг запущен")
      Mine() # Launch mining thread
      debugOutput("авсо майнинг таво")
    except:
      logg = logg + str(now().strftime("%H:%M:%S ") + " net "
      + " Пул не ответил на запрос, перезапуск\n")
      if debug == "True": raise
      Connect()
def loger():
  return(logg)