using System;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Reflection;
using System.Threading;
using System.Windows.Forms;

class Launcher
{
    [STAThread]
    static void Main()
    {
        Application.EnableVisualStyles();
        Application.SetCompatibleTextRenderingDefault(false);

        // Splash form
        Form splash = new Form();
        splash.Text = "M.R.GRAY BOARDS";
        splash.Size = new Size(400, 150);
        splash.StartPosition = FormStartPosition.CenterScreen;
        splash.FormBorderStyle = FormBorderStyle.FixedDialog;
        splash.ControlBox = false;
        splash.ShowInTaskbar = true;
        splash.TopMost = true;

        Label label = new Label();
        label.Text = "M.R.GRAY BOARDS\nInvoice System\n\nStarting server, please wait...";
        label.TextAlign = ContentAlignment.MiddleCenter;
        label.Font = new Font("Arial", 12, FontStyle.Bold);
        label.Dock = DockStyle.Fill;
        splash.Controls.Add(label);

        splash.Shown += (sender, args) =>
        {
            ThreadPool.QueueUserWorkItem(_ =>
            {
                try
                {
                    string exeDir = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
                    string extractDir = Path.Combine(Path.GetTempPath(), "MRGrayBoards");
                    string jarPath = Path.Combine(extractDir, "backend.jar");

                    Directory.CreateDirectory(extractDir);

                    string[] resources = Assembly.GetExecutingAssembly().GetManifestResourceNames();
                    string jarResName = null;
                    string configResName = null;
                    foreach (string r in resources)
                    {
                        if (r.EndsWith("backend.jar")) jarResName = r;
                        if (r.EndsWith("config.properties")) configResName = r;
                    }

                    using (Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream(jarResName ?? "backend.jar"))
                    {
                        if (stream == null) { splash.Invoke(new Action(() => splash.Close())); return; }
                        using (FileStream fs = new FileStream(jarPath, FileMode.Create, FileAccess.Write))
                        {
                            byte[] buffer = new byte[81920];
                            int read;
                            while ((read = stream.Read(buffer, 0, buffer.Length)) > 0)
                                fs.Write(buffer, 0, read);
                        }
                    }

                    string configPath = Path.Combine(exeDir, "config.properties");
                    string[] configLines = null;

                    if (File.Exists(configPath))
                        configLines = File.ReadAllLines(configPath);
                    else if (configResName != null)
                    {
                        using (Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream(configResName))
                        {
                            if (stream != null)
                                using (StreamReader reader = new StreamReader(stream))
                                    configLines = reader.ReadToEnd().Split(new[] { '\r', '\n' }, StringSplitOptions.RemoveEmptyEntries);
                        }
                    }

                    string dbHost = "localhost", dbPort = "3306", dbName = "invoice_db", dbUser = "root", dbPass = "";

                    if (configLines != null)
                    {
                        foreach (string line in configLines)
                        {
                            if (string.IsNullOrWhiteSpace(line) || line.Trim().StartsWith("#")) continue;
                            int eq = line.IndexOf('=');
                            if (eq < 0) continue;
                            string key = line.Substring(0, eq).Trim();
                            string val = line.Substring(eq + 1).Trim();
                            switch (key)
                            {
                                case "DB_HOST": dbHost = val; break;
                                case "DB_PORT": dbPort = val; break;
                                case "DB_NAME": dbName = val; break;
                                case "DB_USERNAME": dbUser = val; break;
                                case "DB_PASSWORD": dbPass = val; break;
                            }
                        }
                    }

                    ProcessStartInfo psi = new ProcessStartInfo();
                    psi.FileName = "java";
                    psi.Arguments = "-jar \"" + jarPath + "\" --DB_HOST=" + dbHost + " --DB_PORT=" + dbPort + " --DB_NAME=" + dbName + " --DB_USERNAME=" + dbUser + " --DB_PASSWORD=" + dbPass;
                    psi.UseShellExecute = true;
                    psi.WindowStyle = ProcessWindowStyle.Hidden;
                    psi.CreateNoWindow = true;

                    try { Process.Start(psi); } catch { }

                    Thread.Sleep(7000);

                    try { Process.Start(new ProcessStartInfo("http://localhost:8080") { UseShellExecute = true }); } catch { }
                }
                catch { }

                splash.Invoke(new Action(() => splash.Close()));
            });
        };

        Application.Run(splash);
    }
}
