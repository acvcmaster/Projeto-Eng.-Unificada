using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;
using FakeClient.Networking;

namespace FakeClient
{
    static class Program
    {
        public static FTP FTPClient;
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            ClienteFake AppForm = new ClienteFake();
            Application.Run(AppForm);
        }
    }
}
